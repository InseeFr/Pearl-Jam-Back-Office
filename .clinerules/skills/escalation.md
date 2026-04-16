# Skill : Protocole d'Escalade

## Objectif

Ce document définit le protocole unique à appliquer quand un workflow atteint
une limite d'itération ou rencontre un blocage. Il est référencé par tous les
workflows et par l'orchestrateur.

## Principe

Aucun agent ne boucle indéfiniment. Dès qu'une limite définie dans le workflow
est atteinte, l'orchestrateur **arrête** la boucle, produit (ou fait produire)
un **rapport de blocage** standard, et attend une décision utilisateur.

## Propriété du protocole

**L'orchestrateur est le propriétaire du protocole d'escalade.** Les agents
individuels sont stateless et ne peuvent pas compter leurs propres invocations
de façon fiable. C'est donc l'orchestrateur qui :

- Tient les compteurs par boucle et le compteur global (25 interventions max par workflow).
- Détecte le franchissement de limite **avant** de relancer un agent.
- Charge ce fichier dans le contexte de l'agent courant au moment du blocage
  (ou produit lui-même le rapport si l'agent n'est pas en mesure de le faire).

Les prompts d'agent individuels n'ont pas besoin de référencer ce document :
l'orchestrateur l'injecte à la demande.

## Règles

1. **Arrêt strict à la limite** : l'orchestrateur ne relance jamais une boucle
   qui franchirait la limite, même si "une tentative de plus pourrait marcher".
2. **Rapport obligatoire** : le template ci-dessous est produit avant de rendre
   la main à l'utilisateur.
3. **Pas de redémarrage silencieux** : aucun agent ne reprend tant que
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

Une **limite globale** de 25 interventions d'agent par workflow
s'applique en complément, pour prévenir les pathologies combinées
(plusieurs boucles qui se relancent mutuellement). Ce seuil tient compte
d'un refactoring multi-modules réaliste (~20 interventions) — voir
`orchestration/coordination.md` pour la justification.

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
