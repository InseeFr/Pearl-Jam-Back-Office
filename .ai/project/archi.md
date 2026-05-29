# Context : Pearl Jam — Architecture hexagonale (état & cible)

## Principes fondamentaux

1. **Direction des dépendances** : tout pointe vers le centre (Domain). Jamais l'inverse.
2. **Ports** : interfaces définies dans le Domain pour communiquer avec l'extérieur.
3. **Adaptateurs** : implémentations concrètes dans `infrastructure-*` ou `api`.

## Structure des modules Maven

```
pearljam-back-office-parent/
├── pearljam-domain-model/              # Value Objects, Enums partagés
├── pearljam-domain/                    # Cœur métier (AUCUNE dép. technique)
│   └── fr.insee.pearljam.domain
│       └── [bounded-context]/
│           ├── port/
│           │   ├── in/                 # Use Cases (interfaces)
│           │   └── out/                # Repositories, clients (interfaces)
│           ├── service/                # Logique métier
│           │   └── exception/          # Exceptions métier
│           ├── model/                  # Entités et Value Objects
│           └── readmodel/              # Projections lecture seule
├── pearljam-api/                       # Adaptateurs entrants (REST)
│   └── fr.insee.pearljam.api
│       └── [bounded-context]/
│           ├── controller/             # @RestController
│           ├── presenter/              # Domain → Response
│           └── response/               # DTOs de réponse (records)
├── pearljam-infrastructure-persistence/# Adaptateurs sortants (DB)
│   └── fr.insee.pearljam.infrastructure.persistence
│       └── [bounded-context]/
│           ├── adapter/                # Implémente les ports out
│           ├── entity/                 # Entités JPA (@Entity)
│           ├── jpa/                    # Spring Data JPA repositories
│           └── mapper/                 # Entity ↔ Domain
├── pearljam-infrastructure-http/       # Adaptateurs sortants HTTP (@HttpExchange)
├── pearljam-infrastructure-security/   # Auth OIDC (Spring Security 7)
├── pearljam-shared-dto/                # DTOs partagés entre modules
└── pearljam-shared-persistence-model/  # Entités persistence partagées
```

## Bounded contexts

| Context            | Package                   | Responsabilité                            |
| ------------------ | ------------------------- | ----------------------------------------- |
| `campaign`         | `domain.campaign`         | Gestion des campagnes d'enquête           |
| `surveyunit`       | `domain.surveyunit`       | Unités d'enquête et cycle de vie          |
| `reporting`        | `domain.reporting`        | Statistiques et rapports                  |
| `organizationunit` | `domain.organizationunit` | Unités organisationnelles et utilisateurs |
| `message`          | `domain.message`          | Messagerie interne                        |
| `security`         | `domain.security`         | Autorisations métier                      |
