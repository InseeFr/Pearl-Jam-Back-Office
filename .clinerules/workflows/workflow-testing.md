# Workflow : workflow-testing

## Déclencheur

L'utilisateur tape `workflow-testing` ou transition depuis `workflow-coding`.

## Diagramme de Flux

```
┌─────────────────┐
│  LeCheckListeur  │ ── Liste les tests nécessaires (couverture 100%)
└────────┬────────┘
         ▼
┌─────────────────┐
│   LeTesteur      │ ── Écrit les tests
└────────┬────────┘
         ▼
┌──────────────────────┐
│ LeSuperviseurDeTache │ ── Exécute les tests
└────────┬─────────────┘
         │
    ┌────┴────┐
    ▼         ▼
  Tests     Tests
  KO        OK
    │         │
    ▼         │
  LeTesteur   │
  corrige     │
  le test     │
    │         │
    └──►─────┘
         │
         ▼
    ┌────┴────┐
    ▼         ▼
  Tests     Tous les
  restants  tests faits
    │         │
    ▼         ▼
  Retour    ✅ Succès
  LeTesteur Informer l'utilisateur
```

## Déroulé Détaillé

### Étape 1 — LeCheckListeur

```
---
[Agent] LeCheckListeur prend la main — Étape 1 du workflow-testing
Objectif : Identifier les tests nécessaires pour couverture 100%
---
```

Actions :
1. Analyser le code de production ajouté/modifié
2. Lister les scénarios de test par fichier :
   - Cas nominal
   - Cas d'erreur (exceptions)
   - Cas limites (null, empty, bornes)
   - Branches conditionnelles
3. Mettre à jour `checklist.md` avec la section tests

**Section ajoutée à la checklist :**
```markdown
## TESTS (LeTesteur)

### 1. Tests Domain — [ServiceName]Test
- [ ] Cas nominal : [description]
- [ ] Cas d'erreur : [exception attendue]
- [ ] Cas limite : null/empty
- [ ] Branches conditionnelles : [description]

### 2. Tests Contrôleur — [ControllerName]Test
- [ ] GET 200 : réponse nominale
- [ ] GET 404 : ressource non trouvée
- [ ] GET 400 : paramètre invalide

### 3. Tests Mapping — [MapperName]Test
- [ ] Entity → Domain
- [ ] Domain → Entity
```

### Étape 2 — LeTesteur

```
---
[Agent] LeTesteur prend la main — Étape 2 du workflow-testing
Objectif : Écrire les tests listés dans la checklist
---
```

Actions :
1. Lire la checklist section tests
2. Créer le Fake Repository si nécessaire
3. Écrire les tests dans l'ordre de la checklist
4. Cocher chaque test écrit

### Étape 3 — LeSuperviseurDeTache

```
---
[Agent] LeSuperviseurDeTache prend la main — Étape 3 du workflow-testing
Objectif : Exécuter les tests et vérifier la complétion
---
```

Actions :
1. Lancer les tests : `./mvnw test -pl [module]`
2. Si tests KO → LeTesteur corrige le test (jamais le code de prod)
3. Si tests OK → vérifier si tous les tests de la checklist sont faits
4. Si tests manquants → retour LeTesteur
5. Si tout est fait → informer l'utilisateur du succès

**Rapport final :**
```
RAPPORT DE TESTS — [Date]

Tests écrits : [N]
Tests passent : [N/N]
Couverture estimée : [%]

Fichiers de test créés :
- [fichier 1]
- [fichier 2]

Workflow-testing terminé avec succès.
```

## Limites d'Itération

| Boucle | Max | Action si dépassé |
|---|---|---|
| Testeur → SuperviseurDeTache → Testeur (test KO) | 5 tours | Escalade utilisateur |
| Testeur → SuperviseurDeTache → Testeur (tests manquants) | 2 tours | Escalade utilisateur |

Quand une limite est atteinte, l'agent courant produit un **rapport de blocage** :

```
BLOCAGE — [NomAgent] — Limite atteinte ([N] tentatives)

Contexte : [ce qui était tenté]
Tentatives :
  1. [action] → [résultat]
  2. [action] → [résultat]

Cause probable : [diagnostic]
Suggestion : [piste pour l'utilisateur]

En attente d'intervention utilisateur.
```

L'orchestrateur présente ce rapport à l'utilisateur. **Aucun agent ne continue tant que l'utilisateur n'a pas décidé.**
