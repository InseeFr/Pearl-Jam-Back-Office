# Skill : Protocole d'Escalade

## Objectif

Ce document définit le protocole unique à appliquer quand un workflow atteint
une limite d'itération ou rencontre un blocage. Il est référencé par tous les
workflows et par l'orchestrateur.

## Principe

Aucun agent ne boucle indéfiniment. Dès qu'une limite définie dans le workflow
est atteinte, l'agent courant **arrête** son action, produit un **rapport de
blocage** standard, et rend la main à l'orchestrateur qui attend une décision
utilisateur.

## Règles

1. **Compter les itérations** : chaque agent impliqué dans une boucle incrémente
   un compteur mental (ou l'indique explicitement dans ses rapports : `tentative 2/3`).
2. **Arrêt strict à la limite** : ne jamais dépasser la limite, même si
   "une tentative de plus pourrait marcher".
3. **Rapport obligatoire** : produire le template ci-dessous avant de rendre la main.
4. **Pas de redémarrage silencieux** : aucun agent ne reprend tant que
   l'utilisateur n'a pas répondu.

## Template de Rapport de Blocage

```
BLOCAGE — [NomAgent] — Limite atteinte ([N] tentatives)

Contexte : [ce qui était tenté]
Tentatives :
  1. [action] → [résultat]
  2. [action] → [résultat]
  3. [action] → [résultat]

Cause probable : [diagnostic]
Suggestion : [piste pour l'utilisateur]

En attente d'intervention utilisateur.
```

## Limites par Workflow (récapitulatif)

Les limites détaillées sont définies dans chaque `workflows/*.md`. Vue d'ensemble :

| Workflow | Boucle | Max |
|---|---|---|
| coding | SuperviseurRegressions ↔ Réparateur | 3 |
| coding | Codeur ↔ SuperviseurDeTache (build KO) | 3 |
| coding | Codeur ↔ SuperviseurDeTache (checklist incomplète) | 2 |
| testing | Testeur ↔ SuperviseurDeTache (test KO) | 3 |
| testing | Testeur ↔ SuperviseurDeTache (tests manquants) | 2 |
| refactoring | RefactoAnalyste ↔ RefactoChallenger (plan rejeté) | 2 |

Une **limite globale** de 15 interventions d'agent par workflow
s'applique en complément, pour prévenir les pathologies combinées
(plusieurs boucles qui se relancent mutuellement).

## Procédure Orchestrateur

Quand un rapport de blocage est reçu :

1. Afficher le rapport à l'utilisateur tel quel.
2. Rappeler les options typiques :
   - Corriger manuellement le point bloquant, puis reprendre.
   - Changer d'approche (ex : simplifier la tâche, la découper).
   - Abandonner le workflow et archiver la checklist en l'état.
3. **Ne rien exécuter** tant que l'utilisateur n'a pas choisi.

## Transitions

- **Entrée** : appelé par tout agent atteignant une limite.
- **Sortie** : retour à l'orchestrateur, qui attend l'utilisateur.
