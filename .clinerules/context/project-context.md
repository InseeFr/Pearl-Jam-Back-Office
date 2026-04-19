# Skill : Contexte Projet — Pearl Jam Back Office

> Source unique sur l'environnement opérationnel : versions, modules,
> commandes build, configuration, profils, Liquibase, Docker, conventions.
> Dernière MAJ : 2026-04-19.

## Versions

| Composant | Version |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.5 |
| Spring Framework | 7 (transitif) |
| Spring Security | 7 (OIDC) |
| Maven | via wrapper `./mvnw` |
| PostgreSQL (local) | via Docker Compose |
| Keycloak (local) | via Docker Compose |

## Package racine

`fr.insee.pearljam`

## Modules Maven

```
pearljam-back-office-parent/
├── pearljam-domain-model              # Value Objects, enums partagés
├── pearljam-domain                    # Logique métier, ports (in/out), services
├── pearljam-shared-dto                # DTOs partagés entre modules
├── pearljam-shared-persistence-model  # Entités persistence partagées
├── pearljam-infrastructure-persistence# Adaptateurs DB (JPA, JdbcClient)
├── pearljam-infrastructure-http       # Clients HTTP sortants (@HttpExchange)
├── pearljam-infrastructure-security   # Auth OIDC, sécurité
├── pearljam-api                       # REST controllers, config Spring, Swagger
└── pearljam-coverage                  # Agrégation JaCoCo
```

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
# Rapport : pearljam-coverage/target/site/jacoco-aggregate/jacoco.xml

# Build complet sans tests
./mvnw compile -pl pearljam-domain,pearljam-api,pearljam-infrastructure-persistence

# Vérif architecture (ArchUnit)
./mvnw test -pl pearljam-api -Dtest=ModuleBoundariesArchTests
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

Propriétés `feature.*` dans `application.yml` :
- `feature.oidc.*` : configuration OIDC (auth-server-host, realm, client-id)
- `feature.swagger.enabled` : active Swagger UI (Springdoc)

## Base de Données & Liquibase

### Emplacement des changelogs

```
pearljam-infrastructure-persistence/src/main/resources/
└── db/
    ├── master.xml              # Point d'entrée Liquibase
    └── changelog/
        ├── 130_notification.xml
        ├── 200_dates.xml
        ├── 520_add_communication_request.xml
        ├── 540_add_communication_template.xml
        ├── 550_add_business_id.xml
        ├── 620_indexes_for_campaign_stats.xml
        ├── 621_indexes_for_closable_su.xml
        ├── 624_create_campaign_daily_stats.xml
        └── NNN_description.xml
```

### Convention de nommage

- Préfixe numérique croissant : `NNN_description.xml`
- Liquibase s'exécute au démarrage de l'application
- Changements de schéma enregistrés via `master.xml`
- **Ne jamais modifier un changelog déjà appliqué** — créer un nouveau fichier

## Suites de Tests

| Classe / Pattern | Type | Module |
|---|---|---|
| `*Test.java` | Unitaire | Tous modules |
| `*IT.java` | Intégration | `pearljam-api`, `pearljam-infrastructure-persistence` |
| `TestAuthKeyCloak` | Suite auth Keycloak | `pearljam-api` |
| `TestNoAuth` | Suite sans auth | `pearljam-api` |
| `CucumberTestRunner` | Runner Cucumber | `pearljam-api` |
| `ModuleBoundariesArchTests` | ArchUnit | `pearljam-api` |

### Cucumber

```
pearljam-api/src/test/resources/features/
└── identification.feature
```

## Infrastructure Locale (Docker)

```
pearljam-api/
├── Dockerfile            # Image de l'API
├── compose.yml           # Stack locale (PostgreSQL + Keycloak)
└── container/            # Scripts et config des conteneurs
```

### Lancer la stack locale

```bash
cd pearljam-api
./mvnw spring-boot:run   # API connectée à la stack docker
```

## Conventions de Code

### Langue

- **Code et commentaires en anglais** (identifiants, Javadoc, messages
  d'exception, messages de commit techniques).
- **Prompts et documentation agents en français** (ce dossier `.clinerules`).

### Nommage

- Package racine : `fr.insee.pearljam`
- Classes : `UpperCamelCase`
- Méthodes/champs : `lowerCamelCase`
- Respecter le nommage existant même si historiquement incorrect
  (exemple : le package `bussinessrules` conserve sa typo).

### Règles de contribution

- **Ne pas reformater** du code non modifié.
- **Conventional Commits** : `feat:`, `fix:`, `chore:`, `docs:`, `test:`, `refactor:`.
- Un commit = un changement focalisé (pas de mélange refactoring + feature).
- Référencer le ticket/issue dans la PR.
- Documenter les changements d'API (exemples requête/réponse).
