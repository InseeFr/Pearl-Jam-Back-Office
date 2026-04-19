# Context : Pearl Jam — Architecture hexagonale (état & cible)

> Source unique sur l'architecture hexagonale du projet : principes, structure,
> règles d'import, patterns en place, violations connues, ordre de migration.
> Dernière MAJ : 2026-04-19.
>
> Les violations listées sont à corriger progressivement via
> `workflow-refactoring`. **Ne pas les reproduire dans du nouveau code.**

## Principes fondamentaux

1. **Direction des dépendances** : tout pointe vers le centre (Domain). Jamais l'inverse.
2. **Ports** : interfaces définies dans le Domain pour communiquer avec l'extérieur.
3. **Adaptateurs** : implémentations concrètes dans `infrastructure-*` ou `api`.
4. **Zéro couplage technique** : le Domain est du Java pur, sans framework.

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

| Context | Package | Responsabilité |
|---|---|---|
| `campaign` | `domain.campaign` | Gestion des campagnes d'enquête |
| `surveyunit` | `domain.surveyunit` | Unités d'enquête et cycle de vie |
| `reporting` | `domain.reporting` | Statistiques et rapports |
| `organizationunit` | `domain.organizationunit` | Unités organisationnelles et utilisateurs |
| `message` | `domain.message` | Messagerie interne |
| `security` | `domain.security` | Autorisations métier |

## Règles d'import (vérification automatique)

### Autorisés

```
pearljam-domain :
  java.* / java.util.*
  fr.insee.pearljam.domain.*
  fr.insee.pearljam.domain.model.* (partagé)

pearljam-api :
  fr.insee.pearljam.domain.*.port.in.*
  fr.insee.pearljam.domain.*.readmodel.*
  fr.insee.pearljam.domain.*.model.*
  org.springframework.web.*
  org.springframework.security.*

pearljam-infrastructure-persistence :
  fr.insee.pearljam.domain.*.port.out.*
  fr.insee.pearljam.domain.*.model.*
  fr.insee.pearljam.domain.*.readmodel.*
  jakarta.persistence.*
  org.springframework.data.*
  org.springframework.jdbc.*
```

### Interdits (violations d'architecture)

```
pearljam-domain NE DOIT JAMAIS importer :
  org.springframework.*
  jakarta.persistence.*
  fr.insee.pearljam.infrastructure.*
  fr.insee.pearljam.api.*
  lombok.*    (voir §Lombok — toléré en transition, interdit dans du code neuf)

pearljam-api NE DOIT JAMAIS importer :
  fr.insee.pearljam.infrastructure.*
  jakarta.persistence.*
  fr.insee.pearljam.domain.*.service.* (sauf via port in)
```

Commandes de vérification :

```bash
grep -rn "import org.springframework" pearljam-domain/src/
grep -rn "import jakarta.persistence" pearljam-domain/src/
grep -rn "import fr.insee.pearljam.infrastructure" pearljam-domain/src/
grep -rn "import fr.insee.pearljam.api" pearljam-domain/src/
```

## Flux d'appel type

```
Client HTTP
    ↓
[pearljam-api] Controller (@RestController)
    ↓ appelle
[pearljam-domain] Port In (interface)
    ↓ implémenté par
[pearljam-domain] Service (logique métier pure)
    ↓ appelle
[pearljam-domain] Port Out (interface)
    ↓ implémenté par
[pearljam-infrastructure-persistence] Adapter (@Repository)
    ↓ utilise
JdbcClient / JpaRepository → Base de données
```

## Structure interne d'un bounded context

```
domain/[context]/
├── port/
│   ├── in/             # Use Cases (interfaces)
│   └── out/            # Repositories, clients (interfaces)
├── service/            # Implémentations
│   └── exception/      # Exceptions métier
├── model/              # Entités et Value Objects
└── readmodel/          # Projections lecture seule
```

## Patterns en place

### Pattern 1 — Reporting (JdbcClient, Spring Boot 4)

Module `reporting` : requêtes analytiques en lecture via `JdbcClient`.

```java
// Port Out (Domain)
public interface CampaignDailyStatsRepositoryPort {
    List<CampaignDailyStats> getCampaignsStats(List<String> campaignIds);
}

// Adaptateur (Infrastructure)
@Repository
@RequiredArgsConstructor
public class CampaignDailyStatsDaoAdapter implements CampaignDailyStatsRepositoryPort {
    private final JdbcClient jdbcClient;

    @Override
    public List<CampaignDailyStats> getCampaignsStats(List<String> campaignIds) {
        return jdbcClient.sql("""
            SELECT campaign_id, campaign_label, total, collected, refusal
            FROM campaign_daily_stats
            WHERE campaign_id IN (:ids)
            """)
            .param("ids", campaignIds)
            .query((rs, _) -> new CampaignDailyStats(
                rs.getString("campaign_id"),
                rs.getString("campaign_label"),
                rs.getLong("total"),
                rs.getLong("collected"),
                rs.getLong("refusal")
            ))
            .list();
    }
}
```

Note Java 25 : paramètre non nommé `_` dans la lambda `RowMapper`.

### Pattern 2 — SurveyUnit (JPA)

Module `surveyunit` : CRUD via Spring Data JPA ; l'adaptateur fait le pont
entité ↔ domain par un mapper.

```java
// Port Out (Domain)
public interface StateRepository {
    List<StateCount> getStateCount(String campaignId);
}

// JPA Repository (Infrastructure)
public interface StateJpaRepository extends JpaRepository<StateDB, Long> {
    @Query("""
        SELECT new fr.insee.pearljam.domain.surveyunit.readmodel.StateCountProjection(
            s.type, COUNT(s)
        )
        FROM StateDB s
        WHERE s.campaignId = :campaignId
        GROUP BY s.type
        """)
    List<StateCountProjection> countByCampaign(String campaignId);
}

// Adaptateur (Infrastructure) — fait le pont
@Repository
@RequiredArgsConstructor
public class StateDaoAdapter implements StateRepository {
    private final StateJpaRepository jpaRepository;

    @Override
    public List<StateCount> getStateCount(String campaignId) {
        return jpaRepository.countByCampaign(campaignId).stream()
            .map(StateMapper::toDomain)
            .toList();
    }
}
```

## Protocole de diagnostic (7 étapes)

| # | Vérification | Méthode |
|---|---|---|
| 1 | Imports interdits dans Domain | `grep -rn "import org.springframework" pearljam-domain/src/` |
| 2 | Driving vs Driven | Le Controller appelle un Port In, jamais un Service directement |
| 3 | Isolation Persistance | Les entités JPA ne sortent jamais de l'adaptateur |
| 4 | Langage Ubiquiste | Pas de noms techniques (`updateInDb`) mais métier (`archive`) |
| 5 | Exceptions étanches | Pas d'exceptions SQL remontées au-delà de l'adaptateur |
| 6 | Testabilité offline | Les tests du Domain ne nécessitent ni DB ni serveur |
| 7 | Config d'injection | Beans déclarés dans `@Configuration`, pas via `@Service` dans le Domain |

## Violations connues (état de migration)

### 1. Annotations Spring dans le Domain — toléré

`@Service`, `@Component`, `@Transactional` présents dans ~15 fichiers du domaine.

Exemples :
- `domain/organizationunit/service/OrganizationUnitServiceImpl` → `@Service` + `@Transactional`
- `domain/surveyunit/service/StateServiceImpl` → `@Service` + `@Transactional`
- `domain/message/service/MessageServiceImpl` → `@Service` + `@Transactional` + `SimpMessagingTemplate` (WebSocket)
- `domain/campaign/service/CurrentDateService` → `@Component`

### 2. Entités JPA dans le Domain — bloquant

Les entités `*DB` (`CampaignDB`, `MessageDB`, `UserDB`, `InterviewerDB`, etc.)
fuient dans des ports et services.

**Ports OUT exposant des entités JPA :**
- `domain/campaign/port/out/CampaignRepository` → `CampaignDB`
- `domain/campaign/port/out/ReferentRepository` → `ReferentDB`
- `domain/message/port/out/MessageRepository` → `MessageDB`
- `domain/message/port/out/MessageStatusRepository` → `MessageStatusDB`
- `domain/organizationunit/port/out/OrganizationUnitRepository` → `OrganizationUnitDB`
- `domain/organizationunit/port/out/UserRepository` → `UserDB`

**Ports IN exposant des entités JPA :**
- `domain/campaign/port/in/CampaignService` → `CampaignDB`

**Services important des entités JPA :**
- `domain/message/service/MessageServiceImpl`
- `domain/campaign/service/PreferenceServiceImpl`

**Cible** : remplacer par modèles domain ou read models. L'entité JPA ne
franchit jamais la frontière de l'adaptateur.

### 3. Types de présentation dans des Ports In — majeur

`HttpStatus` (Spring Web) apparaît dans :
- `domain/organizationunit/port/in/OrganizationUnitService`
- `domain/message/port/in/MessageService`
- `domain/campaign/port/in/PreferenceService`
- `domain/shared/model/Response`

**Cible** : exceptions métier côté domaine, mapping HTTP dans `ExceptionControllerAdvice`.

### 4. Types Spring Data dans ports OUT — majeur

`Pageable` (Spring Data) est exposé dans :
- `domain/campaign/port/out/CampaignRepository`
- `domain/message/service/MessageServiceImpl`

**Cible** : record domain `PageResult<T>` + paramètres primitifs (`int page, int size`).
Voir mémoire projet `project_pageresult.md`.

### 5. Lombok dans le Domain — toléré temporairement

45 occurrences d'`import lombok.*` dans 25 fichiers du domaine
(`@RequiredArgsConstructor`, `@Getter`, `@Setter`).

**Cible** : constructeurs et records explicites. Lombok reste toléré dans
l'infrastructure et l'API pour la transition.

## Exemples observés

### Magic value à remplacer par enum existant

`domain/message/service/MessageServiceImpl:183` :
```java
// AVANT — "REA" littéral, alors que l'enum existe
if (!status.getFirst().equals("REA")) {
    message.setStatus(status.getFirst());
}
```

L'enum existe déjà dans `pearljam-domain-model` :
```java
public enum MessageStatusType { REA, DEL, NRD }
```

Cible : changer le retour du port out (`List<String>` → `List<MessageStatusType>`)
et comparer par identité.

### SRP — boucles imbriquées

`MessageServiceImpl.getMessages(...)` (lignes 160-194) mélange récupération
des OUs, déduplication manuelle, N+1 query sur les statuts, filtrage.
Refactoring : extraire `findVisibleMessageIdsFor` + `applyReadStatus` + port
dédié `findStatusesFor(ids, interviewerId)`.

### Bons exemples à reproduire

**Read models métier :**
```java
// domain/reporting/readmodel/Interviewer.java
public record Interviewer(String id, String label, Long surveyUnitCount) {}

// domain/reporting/readmodel/Referent.java
public record Referent(String firstName, String lastName, String phoneNumber, String role) {}
```

**Exception métier :**
```java
// domain/campaign/service/ReferentServiceImpl:23
if (campaign.isEmpty()) throw new CampaignNotFoundException();

// domain/surveyunit/service/StateServiceImpl:66
throw new InterviewerNotFoundException(interviewerId);
```

**Anti-pattern à ne pas reproduire :**
```java
// Nommage technique dans un domaine
throw new EntityNotFoundException(String.format("Survey unit %s not found", id));
```
Cible : `SurveyUnitNotFoundException` (par bounded context, comme
`CampaignNotFoundException` et `InterviewerNotFoundException`).

## Ordre recommandé de refactoring

Pour attaquer un bounded context :

1. Extraire le modèle domain pur (record ou classe finale) à partir de l'entité JPA.
2. Introduire le mapper Entity ↔ Domain dans l'adaptateur.
3. Remplacer le type de retour du port out (entité → modèle domain).
4. Remplacer le type de retour du port in et du service.
