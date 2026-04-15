# Skill : Clean Code & SOLID

## Objectif

Ce document définit les règles de Clean Code applicables au projet.
Il sert de référence pour les agents LeCodeur, LeTesteur et LeRefactoAnalyste.

## Principes

### SOLID

| Principe | Règle | Exemple projet |
|---|---|---|
| **S**ingle Responsibility | Une classe/méthode = une raison de changer | `StateServiceImpl` ne doit pas gérer auth + stats + mapping |
| **O**pen/Closed | Extensible sans modification | Ajouter un nouveau type d'export sans modifier les existants |
| **L**iskov Substitution | Les sous-types sont interchangeables | Un `FakeRepository` se comporte comme le vrai dans les tests |
| **I**nterface Segregation | Interfaces ciblées, pas de "fat interface" | Séparer `CampaignService` de `CampaignReportingPort` |
| **D**ependency Inversion | Dépendre des abstractions | Le domain dépend de ports, pas d'adaptateurs |

### KISS & YAGNI

- Ne pas créer de classe abstraite "au cas où"
- Préférer un `record` à une classe avec getters/setters
- Ne pas créer de pattern Builder pour un objet à 3 champs

### Clean Code

| Règle | ✅ Bon | ❌ Mauvais |
|---|---|---|
| Nommage expressif | `findActiveInterviewersByCampaign()` | `getData()` |
| Nommage métier | `archiveSurveyUnit()` | `updateStatusInDb()` |
| Pas de magic values | `static final int MAX_RETRY = 3` | `if (count > 3)` |
| Méthode courte | 1 méthode fait 1 chose | Méthode de 80+ lignes |
| Null safety | `Optional`, `List.of()` | Retourner `null` |
| Collections vides | `return List.of()` | `return null` |

## Détection & Correction

### Méthodes surchargées

```java
// ❌ AVANT — mélange validation, logique, I/O
public ResponseEntity<?> exportCsv(String campaignId) {
    if (campaignId == null) throw new BadRequest();      // validation
    var data = repository.findAll(campaignId);            // I/O
    var csv = data.stream().map(this::toCsvLine).toList();// logique
    return ResponseEntity.ok(String.join("\n", csv));     // présentation
}

// ✅ APRÈS — séparation des responsabilités
// Contrôleur : validation + délégation
// Service : logique métier
// Presenter : transformation en CSV
```

### Magic Values

```java
// ❌ AVANT
if (state.equals("ANV")) { ... }

// ✅ APRÈS  
public enum SurveyUnitState {
    ANV("À ne pas visiter"),
    VIN("Visite initiale"),
    // ...
}
if (state == SurveyUnitState.ANV) { ... }
```

### Null Safety

```java
// ❌ AVANT
public List<State> getStates(String campaignId) {
    var result = repository.find(campaignId);
    return result; // peut être null
}

// ✅ APRÈS
public List<State> getStates(String campaignId) {
    var result = repository.find(campaignId);
    return result != null ? result : List.of();
}
```

### Performance

```java
// ❌ Inutile sur une petite collection
data.parallelStream().map(...)

// ✅ Simple et suffisant
data.stream().map(...)
```

## Conventions Java 25

- Utiliser `record` pour les Value Objects, DTOs, Read Models
- Utiliser `sealed interface` quand le nombre de sous-types est fini
- Utiliser le pattern matching dans les `switch` et `instanceof`
- Utiliser les text blocks (`"""`) pour les requêtes SQL
