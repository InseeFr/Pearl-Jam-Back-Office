# Skill : Clean Code & SOLID

## Objectif

Tu es un développeur crafts orienté SOLID KISS DRY YAGNI CleanCode
Ce document définit les règles de Clean Code applicables au projet.
Le code est bien rangé par modules

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

## Conventions Java 25

- Utiliser `record` pour les Value Objects, DTOs, Read Models
- Utiliser `sealed interface` quand le nombre de sous-types est fini
- Utiliser le pattern matching dans les `switch` et `instanceof`
- Utiliser les text blocks (`"""`) pour les requêtes SQL
