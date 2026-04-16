# Workflow : workflow-refactoring

## Déclencheur

L'utilisateur tape `workflow-refactoring` suivi du périmètre à analyser
(fichier, package, module, ou feature).

## Diagramme de Flux

```
┌──────────────────┐
│ LeRefactoAnalyste │ ── Analyse qualité, propose plan
└────────┬─────────┘
         ▼
┌────────────────────────────┐
│ LeRefactoAnalysteChallenger │ ── Challenge le plan
└────────┬───────────────────┘
         │
    ┌────┴────┐
    ▼         ▼
  Plan      Plan
  rejeté    validé
    │         │
    ▼         ▼
  Retour    LeCheckListeur
  Analyste  transforme en tâches
              │
              ▼
         ┌──────────┐
         │ LeCodeur  │ ── Applique le refactoring
         └────┬─────┘
              ▼
      [Boucle workflow-coding]
      (Superviseur Régressions → Superviseur Tâche → Build)
```

## Déroulé Détaillé

### Étape 1 — LeRefactoAnalyste

```
---
[Agent] LeRefactoAnalyste prend la main — Étape 1 du workflow-refactoring
Objectif : Analyser la qualité et proposer un plan d'amélioration
---
```

Actions :
1. Analyser le code selon la grille d'évaluation (voir `skills/refactoring.md`)
2. Produire le diagnostic de santé (note 0-10)
3. Lister les problèmes avec criticité
4. Proposer un plan d'implémentation itératif

### Étape 2 — LeRefactoAnalysteChallenger

```
---
[Agent] LeRefactoAnalysteChallenger prend la main — Étape 2 du workflow-refactoring
Objectif : Challenger le plan pour une qualité optimale
---
```

Actions :
1. Vérifier que chaque itération est atomique
2. Vérifier que l'ordre minimise les régressions
3. Vérifier l'absence de sur-engineering (KISS/YAGNI)
4. Proposer des améliorations ou simplifications
5. Valider ou rejeter le plan

**Si rejeté** → retour à l'étape 1 avec les remarques.
**Si validé** → passage à l'étape 3.

### Étape 3 — LeCheckListeur

```
---
[Agent] LeCheckListeur prend la main — Étape 3 du workflow-refactoring
Objectif : Transformer le plan validé en checklist
---
```

Actions :
1. Transformer chaque itération du plan en tâches de checklist
2. Créer/mettre à jour `checklist.md`
3. Présenter à l'utilisateur pour validation

### Étape 4+ — Boucle workflow-coding

Le refactoring suit ensuite la même logique que `workflow-coding` :
- LeCodeur applique les changements (sans toucher aux tests)
- LeSuperviseurDeRegressions vérifie les régressions
- LeSuperviseurDeTache valide la complétion et le build

## Exemple de Session Complète

```
Utilisateur : workflow-refactoring StateServiceImpl.java

[Agent] LeRefactoAnalyste :
  Note : 4/10
  @Service dans le domaine
  Import d'entité JPA
  Méthode de 76 lignes
  Plan : 3 itérations

[Agent] LeRefactoAnalysteChallenger :
  Itération 1 validée
  Itération 2 : risque sous-estimé → réévalué à Élevé
  Itération 3 validée avec modification

[Agent] LeCheckListeur :
  Checklist créée avec 12 tâches sur 3 itérations

Utilisateur : OK, go

[Agent] LeCodeur :
  Implémentation itération 1...
  [... boucle standard workflow-coding ...]
```

## Limites d'Itération

| Boucle | Max |
|---|---|
| RefactoAnalyste → RefactoChallenger → RefactoAnalyste (plan rejeté) | 2 allers-retours |
| Boucles workflow-coding (Réparateur, Build, Checklist) | Voir `workflow-coding.md` |

Quand une limite est atteinte, appliquer le protocole unifié de
`skills/escalation.md` (template de rapport, règles d'arrêt, procédure
orchestrateur).
