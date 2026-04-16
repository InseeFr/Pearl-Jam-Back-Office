# Skill : Architecture Hexagonale — Pearl Jam Back Office

## Objectif

Ce document décrit l'architecture hexagonale cible du projet et sert de référence
pour tous les agents qui produisent ou analysent du code.

## Principes Fondamentaux

1. **Direction des dépendances** : Tout pointe vers le centre (Domain). Jamais l'inverse.
2. **Ports** : Interfaces définies dans le Domain pour communiquer avec l'extérieur
3. **Adaptateurs** : Implémentations concrètes dans Infrastructure/API
4. **Zéro couplage technique** : Le Domain est du Java pur, sans framework

## Structure des Modules

```
pearljam-back-office-parent/
├── pearljam-domain-model/          # Value Objects, Enums partagés
├── pearljam-domain/                # Cœur métier (AUCUNE dépendance technique)
│   └── fr.insee.pearljam.domain
│       └── [bounded-context]/
│           ├── port/
│           │   ├── in/             # Interfaces entrantes (Use Cases)
│           │   └── out/            # Interfaces sortantes (Repositories, Clients)
│           ├── service/            # Implémentation de la logique métier
│           │   └── exception/      # Exceptions métier
│           ├── model/              # Entités et Value Objects du domaine
│           └── readmodel/          # Read Models (projections lecture seule)
├── pearljam-api/                   # Adaptateurs entrants (REST)
│   └── fr.insee.pearljam.api
│       └── [bounded-context]/
│           ├── controller/         # @RestController
│           ├── presenter/          # Transformation Domain → Response
│           └── response/           # DTOs de réponse (records)
├── pearljam-infrastructure-persistence/  # Adaptateurs sortants (DB)
│   └── fr.insee.pearljam.infrastructure.persistence
│       └── [bounded-context]/
│           ├── adapter/            # Implémente les ports out
│           ├── entity/             # Entités JPA (@Entity)
│           ├── jpa/                # Spring Data JPA repositories
│           └── mapper/             # Conversion Entity ↔ Domain
├── pearljam-infrastructure-http/   # Adaptateurs sortants (HTTP)
├── pearljam-infrastructure-security/ # Sécurité (auth, OIDC)
├── pearljam-shared-dto/            # DTOs partagés entre modules
└── pearljam-shared-persistence-model/ # Entités persistence partagées
```

## Bounded Contexts Identifiés

| Context | Package | Responsabilité |
|---|---|---|
| `campaign` | `domain.campaign` | Gestion des campagnes d'enquête |
| `surveyunit` | `domain.surveyunit` | Unités d'enquête et leur cycle de vie |
| `reporting` | `domain.reporting` | Statistiques et rapports |
| `organizationunit` | `domain.organizationunit` | Unités organisationnelles et utilisateurs |
| `message` | `domain.message` | Messagerie interne |
| `security` | `domain.security` | Autorisations métier |

## Règles d'Import (VÉRIFICATION AUTOMATIQUE)

### Imports AUTORISÉS

```
pearljam-domain peut importer :
  java.* / java.util.*
  fr.insee.pearljam.domain.*
  fr.insee.pearljam.domain.model.* (partagé)

pearljam-api peut importer :
  fr.insee.pearljam.domain.*.port.in.*
  fr.insee.pearljam.domain.*.readmodel.*
  fr.insee.pearljam.domain.*.model.*
  org.springframework.web.*
  org.springframework.security.*

pearljam-infrastructure-persistence peut importer :
  fr.insee.pearljam.domain.*.port.out.*
  fr.insee.pearljam.domain.*.model.*
  fr.insee.pearljam.domain.*.readmodel.*
  jakarta.persistence.*
  org.springframework.data.*
  org.springframework.jdbc.*
```

### Imports INTERDITS (violations d'architecture)

```
pearljam-domain NE DOIT JAMAIS importer :
  org.springframework.*
  jakarta.persistence.*
  fr.insee.pearljam.infrastructure.*
  fr.insee.pearljam.api.*
  lombok.* (toléré pour @RequiredArgsConstructor en transition)

pearljam-api NE DOIT JAMAIS importer :
  fr.insee.pearljam.infrastructure.*
  jakarta.persistence.*
  fr.insee.pearljam.domain.*.service.* (sauf via port in)
```

## Flux d'Appel Type

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

## Patterns du Projet

### Pattern 1 : Reporting (JdbcClient)

Le module reporting utilise JdbcClient pour les requêtes de lecture :

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
            FROM campaign_daily_stats WHERE campaign_id IN (:ids)
            """)
            .param("ids", campaignIds)
            .query((rs, _) -> new CampaignDailyStats(...))
            .list();
    }
}
```

### Pattern 2 : SurveyUnit (JPA)

Le module surveyunit utilise JPA pour les opérations CRUD :

```java
// Port Out (Domain)
public interface StateRepository {
    List<StateCount> getStateCount(String campaignId);
}

// JPA Repository (Infrastructure)
public interface StateJpaRepository extends JpaRepository<StateDB, Long> {
    @Query("SELECT new ...StateCountProjection(...) FROM StateDB s WHERE ...")
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

## Protocole de Diagnostic (7 Étapes)

| Étape | Vérification | Commande |
|---|---|---|
| 1 | Imports interdits dans Domain | `grep -rn "import org.springframework" pearljam-domain/src/` |
| 2 | Driving vs Driven | Le Controller appelle un Port In, jamais un Service directement |
| 3 | Isolation Persistance | Les entités JPA ne sortent jamais de l'adaptateur |
| 4 | Langage Ubiquiste | Pas de noms techniques (`updateInDb`) mais métier (`archive`) |
| 5 | Exceptions étanches | Pas d'exceptions SQL remontées au-delà de l'adaptateur |
| 6 | Testabilité offline | Les tests du Domain ne nécessitent ni DB ni serveur |
| 7 | Configuration d'injection | Les beans sont déclarés dans `@Configuration`, pas via `@Service` dans le Domain |

## Points d'Attention (État Actuel)

Le projet est en cours de migration vers une architecture hexagonale pure.
Les violations suivantes existent dans le code et sont à corriger
progressivement via `workflow-refactoring`. **Ne pas les reproduire dans
du nouveau code.**

### 1. Annotations Spring dans le Domain (bloquant à terme)

`@Service`, `@Component`, `@Transactional` et autres annotations Spring sont
présentes dans de nombreux services du domaine (~15 fichiers). Exemples :

```
pearljam-domain/.../organizationunit/service/OrganizationUnitServiceImpl.java
  → @Service + @Transactional
pearljam-domain/.../surveyunit/service/StateServiceImpl.java
  → @Service + @Transactional
pearljam-domain/.../message/service/MessageServiceImpl.java
  → @Service + @Transactional + SimpMessagingTemplate (WebSocket Spring)
pearljam-domain/.../campaign/service/CurrentDateService.java
  → @Component
```

**Cible** : déclarer les beans dans une `@Configuration` côté infrastructure
ou API. Le domaine reste du Java pur.

### 2. Entités JPA référencées depuis le Domain (bloquant)

Les entités `*DB` (CampaignDB, MessageDB, UserDB, InterviewerDB, etc.)
fuient dans des ports et services du domaine :

```
Ports OUT qui exposent des entités JPA :
  pearljam-domain/.../campaign/port/out/CampaignRepository.java    → CampaignDB
  pearljam-domain/.../campaign/port/out/ReferentRepository.java    → ReferentDB
  pearljam-domain/.../message/port/out/MessageRepository.java      → MessageDB
  pearljam-domain/.../message/port/out/MessageStatusRepository.java → MessageStatusDB
  pearljam-domain/.../organizationunit/port/out/OrganizationUnitRepository.java → OrganizationUnitDB
  pearljam-domain/.../organizationunit/port/out/UserRepository.java → UserDB

Ports IN qui exposent des entités JPA :
  pearljam-domain/.../campaign/port/in/CampaignService.java        → CampaignDB

Services qui importent des entités JPA :
  pearljam-domain/.../message/service/MessageServiceImpl.java
  pearljam-domain/.../campaign/service/PreferenceServiceImpl.java
```

**Cible** : remplacer par des modèles domain ou des read models
(voir `skills/refactoring.md` §R1 et §R4). L'entité JPA ne franchit
jamais la frontière de l'adaptateur.

### 3. Types de présentation dans des Ports In (majeur)

`HttpStatus` (type Spring Web) apparaît dans plusieurs ports in et
services du domaine :

```
pearljam-domain/.../organizationunit/port/in/OrganizationUnitService.java
pearljam-domain/.../message/port/in/MessageService.java
pearljam-domain/.../campaign/port/in/PreferenceService.java
pearljam-domain/.../shared/model/Response.java
```

**Cible** : les exceptions métier sont levées par le domaine, le mapping
vers un code HTTP est fait par l'`ExceptionControllerAdvice` côté API.

### 4. Types Spring Data dans les ports OUT (majeur)

`Pageable` (Spring Data) est exposé dans `CampaignRepository` (port out)
et dans `MessageServiceImpl`. `Pageable` est un concept d'infrastructure
qui ne doit pas traverser la frontière.

**Cible** : utiliser le record domain `PageResult<T>` + des paramètres
primitifs (`int page, int size`) dans les ports out. Voir aussi la
mémoire projet sur ce sujet.

### 5. Lombok dans le Domain (toléré temporairement)

45 occurrences de `import lombok.*` dans 25 fichiers du domaine,
principalement `@RequiredArgsConstructor`, `@Getter`, `@Setter`.

**Cible** : constructeurs et records explicites. Lombok reste toléré
dans l'infrastructure et l'API pour la transition.

## Ordre Recommandé de Refactoring

Quand on s'attaque à un bounded context, l'ordre qui minimise les
régressions est :

1. Extraire le modèle domain pur à partir de l'entité JPA (record ou classe finale).
2. Introduire le mapper Entity ↔ Domain dans l'adaptateur.
3. Remplacer le type de retour du port out (entité → modèle domain).
4. Remplacer le type de retour du port in et du service.
5. Retirer `@Service` / `@Transactional` du service domain (les déplacer en `@Configuration`).
6. Retirer Lombok en dernier (le moins risqué, mais le plus verbeux).
