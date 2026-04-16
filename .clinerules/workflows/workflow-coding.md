# Workflow : workflow-coding

## Déclencheur

L'utilisateur tape `workflow-coding` suivi d'une description de la feature à implémenter.

## Diagramme de Flux

```
┌─────────────────┐
│  LeCheckListeur  │ ── Analyse la demande, crée checklist.md
└────────┬────────┘
         ▼
┌─────────────────┐
│    LeCodeur      │ ── Implémente les tâches séquentiellement
└────────┬────────┘
         ▼
┌──────────────────────────────┐
│ LeSuperviseurDeRegressions   │ ── Lance les tests existants
└────────┬─────────────────────┘
         │
    ┌────┴────┐
    ▼         ▼
  Tests     Tests
  KO        OK
    │         │
    ▼         │
┌──────────┐  │
│LeRéparateur│ │
└────┬─────┘  │
     │        │
     └──►─────┘
         │
         ▼
┌──────────────────────┐
│ LeSuperviseurDeTache │ ── Vérifie checklist + build
└────────┬─────────────┘
         │
    ┌────┴────┐
    ▼         ▼
  Checklist  Tout
  incomplète complété
    │         │
    ▼         ▼
  Retour    Build Maven
  LeCodeur    │
         ┌───┴───┐
         ▼       ▼
       Build    Build
       KO       OK
         │       │
         ▼       ▼
       LeCodeur  LeCheckListeur
       répare    propose workflow-testing
                 (attend validation utilisateur)
```

## Déroulé Détaillé

### Étape 1 — LeCheckListeur

**Entrée** : Demande utilisateur
**Sortie** : `checklist.md` créée/mise à jour

```
---
[Agent] LeCheckListeur prend la main — Étape 1 du workflow-coding
Objectif : Analyser la demande et créer la checklist
---
```

Actions :
1. Analyser le code existant lié à la demande
2. Identifier les couches impactées (Domain, Infrastructure, API)
3. Identifier les modules Maven impactés
4. Créer `checklist.md` avec les tâches séquencées
5. Présenter la checklist à l'utilisateur pour validation

**Point de contrôle** : L'utilisateur valide la checklist avant de continuer.

### Étape 2 — LeCodeur

**Entrée** : `checklist.md` validée
**Sortie** : Code de production implémenté

```
---
[Agent] LeCodeur prend la main — Étape 2 du workflow-coding
Objectif : Implémenter les tâches de la checklist
---
```

Actions :
1. Lire la checklist
2. Prendre la première tâche non cochée
3. Implémenter le code
4. Cocher la tâche
5. Recommencer jusqu'à ce que toutes les tâches soient cochées

**Ordre d'implémentation** : Domain → Infrastructure → API (toujours).

### Étape 3 — LeSuperviseurDeRegressions

**Entrée** : Code implémenté
**Sortie** : Rapport de régression

```
---
[Agent] LeSuperviseurDeRegressions prend la main — Étape 3 du workflow-coding
Objectif : Vérifier qu'aucune régression n'a été introduite
---
```

Actions :
1. Lancer `./mvnw test` sur les modules impactés
2. Analyser chaque test en échec
3. Catégoriser : Régression vs Évolution légitime

**Branche 3A — Régression détectée** :
→ LeRéparateur corrige le code
→ Retour à l'étape 3 (re-vérification)

**Branche 3B — Pas de régression** :
→ Passer à l'étape 4

### Étape 4 — LeSuperviseurDeTache

**Entrée** : Tests OK
**Sortie** : Validation ou retour en boucle

```
---
[Agent] LeSuperviseurDeTache prend la main — Étape 4 du workflow-coding
Objectif : Vérifier la complétion et lancer le build
---
```

Actions :
1. Vérifier que toutes les tâches de `checklist.md` sont cochées
2. Vérifier les imports interdits dans le domaine
3. Lancer le build Maven complet

**Branche 4A — Checklist incomplète** :
→ Retour vers LeCodeur (étape 2)

**Branche 4B — Build KO** :
→ LeCodeur répare → Retour étape 4

**Branche 4C — Tout OK** :
→ LeCheckListeur propose `workflow-testing`
→ **ATTEND la validation utilisateur** (ne lance jamais automatiquement)

## Limites d'Itération

| Boucle | Max | Action si dépassé |
|---|---|---|
| SuperviseurRegressions → Réparateur → SuperviseurRegressions | 3 tours | Escalade utilisateur |
| Codeur → SuperviseurDeTache → Codeur (build KO) | 3 tours | Escalade utilisateur |
| Codeur → SuperviseurDeTache → Codeur (checklist incomplète) | 2 tours | Escalade utilisateur |

Quand une limite est atteinte, l'agent courant produit un **rapport de blocage** :

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

L'orchestrateur présente ce rapport à l'utilisateur. **Aucun agent ne continue tant que l'utilisateur n'a pas décidé.**

---

## Commandes Maven Utilisées

```bash
# Tests d'un module spécifique
./mvnw test -pl pearljam-domain

# Build complet sans tests
./mvnw compile -pl pearljam-domain,pearljam-api,pearljam-infrastructure-persistence

# Build complet avec tests
./mvnw verify

# Vérification d'architecture (ArchUnit)
./mvnw test -pl pearljam-api -Dtest=ArchitectureTest
```
