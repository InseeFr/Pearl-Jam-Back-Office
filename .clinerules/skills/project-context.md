# Skill : Contexte Projet — Pearl Jam Back Office

## Objectif

Ce document capture les informations opérationnelles du projet : commandes de build,
configuration, profils de test, infrastructure locale. Il sert de référence pour tous
les agents quand ils doivent exécuter des commandes ou comprendre l'environnement.

## Versions

| Composant | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.5 |
| Maven (wrapper) | via `./mvnw` |
| PostgreSQL (local) | via Docker Compose |
| Keycloak (local) | via Docker Compose |

## Commandes Build & Run

```bash
# Build complet (tous modules + tous tests)
./mvnw clean install

# Build un module spécifique
./mvnw clean install -pl pearljam-domain

# Tests Surefire uniquement
./mvnw test

# Test d'une classe spécifique
./mvnw -Dtest=MyTest test

# Lancer l'API localement
./mvnw spring-boot:run -pl pearljam-api

# Build JAR sans tests
./mvnw clean package -DskipTests

# Couverture JaCoCo
./mvnw -Pcoverage test
# Rapport dans pearljam-coverage/target/site/jacoco-aggregate/jacoco.xml
```

## Modules Maven

```
pearljam-back-office-parent/
├── pearljam-domain-model          # Value Objects, enums partagés
├── pearljam-domain                # Logique métier, ports (in/out), services
├── pearljam-shared-dto            # DTOs partagés entre modules
├── pearljam-shared-persistence-model # Entités persistence partagées
├── pearljam-infrastructure-persistence # Adaptateurs DB (JPA, JdbcClient)
├── pearljam-infrastructure-http   # Clients HTTP sortants
├── pearljam-infrastructure-security # Auth OIDC, sécurité
├── pearljam-api                   # REST controllers, config Spring, Swagger
└── pearljam-coverage              # Agrégation JaCoCo
```

## Configuration & Profils

### Fichiers de configuration

| Fichier | Emplacement | Usage |
|---|---|---|
| `application.yml` | `pearljam-api/src/main/resources/` | Config principale |
| `application-docker.yml` | `pearljam-api/src/main/resources/` | Config Docker local |
| `application-auth.yml` | `pearljam-api/src/test/resources/` | Tests avec auth Keycloak |
| `application-noauth.yml` | `pearljam-api/src/test/resources/` | Tests sans auth |
| `application-demo.yml` | `pearljam-api/src/test/resources/` | Tests avec données démo |
| `application-auth.yml` | `pearljam-infrastructure-persistence/src/test/resources/` | Tests persistence avec auth |

### Feature flags

Les fonctionnalités sont activées via des propriétés `feature.*` dans `application.yml` :
- `feature.oidc.*` : configuration OIDC (auth-server-host, realm, client-id)
- `feature.swagger.enabled` : active Swagger UI (Springdoc)

## Base de Données & Liquibase

### Emplacement des changelogs

```
pearljam-infrastructure-persistence/src/main/resources/
└── db/
    ├── master.xml              # Point d'entrée Liquibase (inclut les changelogs)
    └── changelog/
        ├── 130_notification.xml
        ├── 200_dates.xml
        ├── 520_add_communication_request.xml
        ├── 540_add_communication_template.xml
        ├── 550_add_business_id.xml
        ├── 620_indexes_for_campaign_stats.xml
        ├── 621_indexes_for_closable_su.xml
        ├── 624_create_campaign_daily_stats.xml
        └── ...
```

### Convention de nommage des changelogs

- Préfixe numérique croissant : `NNN_description.xml`
- Liquibase s'exécute au démarrage de l'application
- Les changements de schéma doivent être enregistrés via `master.xml`
- **Ne jamais modifier un changelog déjà appliqué** — créer un nouveau fichier

## Suites de Tests

### Tests unitaires et d'intégration

| Classe / Pattern | Type | Module |
|---|---|---|
| `*Test.java` | Unitaire | Tous modules |
| `*IT.java` | Intégration | `pearljam-api`, `pearljam-infrastructure-persistence` |
| `TestAuthKeyCloak` | Suite auth Keycloak | `pearljam-api` |
| `TestNoAuth` | Suite sans auth | `pearljam-api` |
| `CucumberTestRunner` | Runner Cucumber | `pearljam-api` |
| `ModuleBoundariesArchTests` | Tests ArchUnit | `pearljam-api` |

### Fichiers Cucumber

```
pearljam-api/src/test/resources/features/
└── identification.feature
```

## Infrastructure Locale (Docker)

```
pearljam-api/
├── Dockerfile            # Image de l'API
├── compose.yml           # Stack locale (PostgreSQL + Keycloak)
└── container/            # Scripts et config pour les conteneurs
```

### Lancer la stack locale

```bash
cd pearljam-api
./mvnw spring-boot:run   # Lance l'API connectée à la stack
```

## Conventions de Code

### Nommage

- Package racine : `fr.insee.pearljam`
- Classes : `UpperCamelCase`
- Méthodes/champs : `lowerCamelCase`
- Respecter le nommage existant même si historiquement incorrect
  (exemple : le package `bussinessrules` conserve sa typo)

### Règles de contribution

- **Ne pas reformater** du code non modifié
- **Conventional Commits** : `feat:`, `fix:`, `chore:`, `docs:`, `test:`
- Un commit = un changement focalisé (pas de mélange refactoring + feature)
- Référencer le ticket/issue dans la PR
- Documenter les changements d'API (exemples requête/réponse)
