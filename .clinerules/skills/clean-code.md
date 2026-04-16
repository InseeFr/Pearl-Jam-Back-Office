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

| Règle | Bon | Mauvais |
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
// AVANT — mélange validation, logique, I/O
public ResponseEntity<?> exportCsv(String campaignId) {
    if (campaignId == null) throw new BadRequest();      // validation
    var data = repository.findAll(campaignId);            // I/O
    var csv = data.stream().map(this::toCsvLine).toList();// logique
    return ResponseEntity.ok(String.join("\n", csv));     // présentation
}

// APRÈS — séparation des responsabilités
// Contrôleur : validation + délégation
// Service : logique métier
// Presenter : transformation en CSV
```

### Magic Values

```java
// AVANT
if (state.equals("ANV")) { ... }

// APRÈS
public enum SurveyUnitState {
    ANV("À ne pas visiter"),
    VIN("Visite initiale"),
    // ...
}
if (state == SurveyUnitState.ANV) { ... }
```

### Null Safety

```java
// AVANT
public List<State> getStates(String campaignId) {
    var result = repository.find(campaignId);
    return result; // peut être null
}

// APRÈS
public List<State> getStates(String campaignId) {
    var result = repository.find(campaignId);
    return result != null ? result : List.of();
}
```

### Performance

```java
// Inutile sur une petite collection
data.parallelStream().map(...)

// Simple et suffisant
data.stream().map(...)
```

## Exemples observés dans Pearl Jam

Les snippets ci-dessous sont tirés du code actuel et servent de repères
concrets pour le Codeur, le Testeur et le RefactoAnalyste.

### Magic value — statuts de messagerie

Observé dans `pearljam-domain/.../message/service/MessageServiceImpl.java:183` :

```java
// AVANT — "REA" est un littéral non typé, dupliqué ailleurs dans le module
if (!status.getFirst().equals("REA")) {
    message.setStatus(status.getFirst());
} else {
    messagesDeleted.add(message);
}
```

Refactoring cible : introduire un enum `MessageStatus` avec une valeur
`READ("REA")` et comparer par identité. Les autres statuts (non lus,
archivés…) deviennent explicites.

### SRP — boucles imbriquées dans un service

`MessageServiceImpl.getMessages(...)` (lignes 160-194) mélange :

- récupération des OUs d'un utilisateur via `userService` ;
- dé-duplication manuelle d'IDs via une liste + `contains` ;
- récupération de chaque statut en boucle (N+1 query) ;
- filtrage des messages "supprimés" par exclusion.

Refactoring cible : extraire `findVisibleMessageIdsFor(interviewerId)`
et `applyReadStatus(messages, interviewerId)`. La requête N+1 se
remplace par un port dédié `MessageStatusRepository.findStatusesFor(ids, interviewerId)`.

### Nommage métier — bons exemples à reproduire

Les read models de reporting illustrent le nommage métier cible :

```java
// pearljam-domain/.../reporting/readmodel/Interviewer.java
public record Interviewer(String id, String label, Long surveyUnitCount) {}

// pearljam-domain/.../reporting/readmodel/Referent.java
public record Referent(String firstName, String lastName, String phoneNumber, String role) {}
```

Trois points à reproduire :

- `record` plutôt que classe + getters/setters.
- Nom du type au singulier, sans suffixe technique (`Dto`, `Bean`).
- Pas d'annotation Spring/JPA — pur modèle domain.

### Null Safety — collection vide vs null

Observé à la fois dans les adaptateurs JdbcClient et dans certains
services : `.list()` de `JdbcClient` ne retourne jamais `null`. À
l'inverse, certains anciens codes retournent `null` au lieu de
`List.of()`. Règle absolue côté domain :

```java
// Règle : un port out retourne toujours une List non nulle
public interface CampaignRepository {
    List<Campaign> findByUser(String userId); // jamais null, List.of() si vide
}
```

### Exception métier — bons exemples à reproduire

Le pattern cible est appliqué dans plusieurs services :

```java
// pearljam-domain/.../campaign/service/ReferentServiceImpl.java:23
if (campaign.isEmpty()) {
    throw new CampaignNotFoundException();
}

// pearljam-domain/.../surveyunit/service/StateServiceImpl.java:66
throw new InterviewerNotFoundException(interviewerId);
```

L'`ExceptionControllerAdvice` côté API convertit ces exceptions en
404 `ApiError` — le service ne manipule aucun code HTTP.

Anti-pattern à ne pas reproduire (même fichier, ligne 119) :

```java
// AVANT — nommage technique (Entity) dans une exception du domaine
// Importée depuis fr.insee.pearljam.domain.shared.exception — la classe
// est bien dans le domaine mais son nom parle d'entité, pas de métier
throw new EntityNotFoundException(String.format("Survey unit %s not found", id));
```

Cible : remplacer par `SurveyUnitNotFoundException` (exception métier
du bounded context `surveyunit`, comme `CampaignNotFoundException` et
`InterviewerNotFoundException` déjà en place).

## Conventions Java 25

- Utiliser `record` pour les Value Objects, DTOs, Read Models
- Utiliser `sealed interface` quand le nombre de sous-types est fini
- Utiliser le pattern matching dans les `switch` et `instanceof`
- Utiliser les text blocks (`"""`) pour les requêtes SQL
