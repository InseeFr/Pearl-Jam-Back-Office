# Plan de Refactoring vers Architecture Hexagonale - Pearl-Jam-Back-Office

## Contexte

Le projet Pearl-Jam-Back-Office (Spring Boot 3.5.5 / Java 21) est **partiellement migre** vers une architecture hexagonale. Sur 300 classes Java :
- **api/** (192 fichiers) : couche legacy monolithique (controllers, services, repositories JPA, DTOs, entites JPA)
- **domain/** (60 fichiers) : nouvelle couche domaine (partiellement implementee pour campaign/visibility, surveyunit/comment, security)
- **infrastructure/** (47 fichiers) : adaptateurs et entites DB (partiellement implementee)

### Problemes identifies

1. **Couplage direct JPA** : 14 repositories JPA dans `api/repository/` injectes directement dans 13 services
2. **Pas d'inversion de dependance** : les services appellent les repos JPA directement au lieu de passer par des ports domaine
3. **Patterns mixtes** : certains agregats (Visibility, Comment) utilisent ports + adaptateurs, la majorite non
4. **Violation cross-layer** : `InterviewerServiceImpl` injecte `VisibilityJpaRepository` (couche infrastructure)
5. **Entites JPA dans api/** : 71 entites JPA dans `api/domain/` utilisees directement dans les services
6. **0 tests ArchUnit** actifs pour enforcer les regles d'architecture

### Pattern de reference existant (a reproduire)

Le pattern cible est deja etabli dans le code :
- **Modele domaine** : `domain/campaign/model/Visibility.java` (Java record, pure logique metier)
- **Port serverside** : `domain/campaign/port/serverside/VisibilityRepository.java` (interface)
- **Port userside** : `domain/campaign/port/userside/VisibilityService.java` (interface)
- **Service domaine** : `domain/campaign/service/VisibilityServiceImpl.java` (@Service, injecte seulement des ports)
- **Adaptateur** : `infrastructure/campaign/adapter/VisibilityDaoAdapter.java` (@Repository, implemente le port serverside)
- **Entite DB** : `infrastructure/campaign/entity/VisibilityDB.java` (@Entity JPA, methodes toModel/fromModel)
- **Repository JPA** : `infrastructure/campaign/jpa/VisibilityJpaRepository.java` (Spring Data)

---

## Bounded Contexts et Agregats

| Bounded Context | Racine d'agregat | Sous-entites / Value Objects |
|---|---|---|
| **campaign** | Campaign | Visibility, CommunicationTemplate, Referent |
| **surveyunit** | SurveyUnit | State, Address, SampleIdentifier, ContactOutcome, ClosingCause, Identification, Comment, CommunicationRequest, ContactAttempt, ContactHistory, Person, TempZone |
| **interviewer** | Interviewer | (standalone) |
| **organizationunit** | OrganizationUnit | (hierarchie parent-enfant) |
| **user** | User | Preference (User-Campaign) |
| **message** | Message | MessageStatus, recipients |
| **security** | (cross-cutting) | AuthorityRole, AuthenticatedUser (deja migre) |

---

## Arborescence Cible

```
src/main/java/fr/insee/pearljam/
├── PearlJamApplication.java
│
├── domain/                                    # ZERO dependance vers api/ ou infrastructure/
│   ├── campaign/
│   │   ├── model/
│   │   │   ├── Campaign.java                  [DONE - record]
│   │   │   ├── CampaignVisibility.java        [EXISTS]
│   │   │   ├── Referent.java                  [DONE - record]
│   │   │   ├── Visibility.java                [EXISTS]
│   │   │   └── communication/                 [EXISTS]
│   │   ├── port/
│   │   │   ├── serverside/
│   │   │   │   ├── CampaignRepository.java    [DONE - rempli]
│   │   │   │   ├── ReferentRepository.java    [NEW]
│   │   │   │   ├── CommunicationTemplateRepository.java [EXISTS]
│   │   │   │   └── VisibilityRepository.java  [EXISTS]
│   │   │   └── userside/
│   │   │       ├── CampaignService.java       [EXISTS - interface]
│   │   │       ├── ReferentService.java       [EXISTS - interface]
│   │   │       ├── CommunicationTemplateService.java [EXISTS]
│   │   │       ├── DateService.java           [EXISTS]
│   │   │       └── VisibilityService.java     [EXISTS]
│   │   └── service/
│   │       ├── CampaignServiceImpl.java       [NEW - logique metier extraite de api/]
│   │       ├── ReferentServiceImpl.java       [NEW]
│   │       ├── CommunicationTemplateServiceImpl.java [EXISTS]
│   │       ├── CurrentDateService.java        [EXISTS]
│   │       └── VisibilityServiceImpl.java     [EXISTS]
│   │
│   ├── surveyunit/
│   │   ├── model/
│   │   │   ├── SurveyUnit.java                [NEW record]
│   │   │   ├── State.java                     [NEW record]
│   │   │   ├── StateType.java                 [MOVE depuis api/domain/]
│   │   │   ├── Address.java                   [NEW record]
│   │   │   ├── SampleIdentifier.java          [NEW record]
│   │   │   ├── ClosingCause.java              [NEW record]
│   │   │   ├── ClosingCauseType.java          [MOVE depuis api/domain/]
│   │   │   ├── ContactAttempt.java            [NEW record]
│   │   │   ├── ContactOutcome.java            [EXISTS]
│   │   │   ├── ContactOutcomeType.java        [MOVE depuis api/domain/]
│   │   │   ├── Comment.java                   [EXISTS]
│   │   │   ├── Identification.java            [EXISTS]
│   │   │   ├── SurveyUnitForInterviewer.java  [EXISTS]
│   │   │   ├── communication/                 [EXISTS]
│   │   │   ├── contacthistory/                [EXISTS]
│   │   │   └── question/                      [EXISTS]
│   │   ├── port/
│   │   │   ├── serverside/
│   │   │   │   ├── SurveyUnitRepository.java  [NEW]
│   │   │   │   ├── StateRepository.java       [NEW]
│   │   │   │   ├── AddressRepository.java     [NEW]
│   │   │   │   ├── ClosingCauseRepository.java [NEW]
│   │   │   │   ├── ContactOutcomeRepository.java [NEW]
│   │   │   │   ├── CommentRepository.java     [EXISTS]
│   │   │   │   └── CommunicationRequestRepository.java [EXISTS]
│   │   │   └── userside/
│   │   │       ├── SurveyUnitService.java     [NEW interface]
│   │   │       ├── SurveyUnitUpdateService.java [NEW interface]
│   │   │       ├── StateService.java          [NEW interface]
│   │   │       ├── ContactOutcomeService.java [NEW interface]
│   │   │       ├── ClosingCauseService.java   [NEW interface]
│   │   │       └── CommentService.java        [EXISTS]
│   │   └── service/
│   │       ├── SurveyUnitServiceImpl.java     [NEW]
│   │       ├── SurveyUnitUpdateServiceImpl.java [NEW]
│   │       ├── StateServiceImpl.java          [NEW]
│   │       ├── ContactOutcomeServiceImpl.java [NEW]
│   │       ├── ClosingCauseServiceImpl.java   [NEW]
│   │       └── CommentServiceImpl.java        [EXISTS]
│   │
│   ├── interviewer/
│   │   ├── model/
│   │   │   ├── Interviewer.java               [NEW record]
│   │   │   └── Title.java                     [MOVE depuis api/domain/]
│   │   ├── port/
│   │   │   ├── serverside/
│   │   │   │   └── InterviewerRepository.java [NEW]
│   │   │   └── userside/
│   │   │       └── InterviewerService.java    [NEW interface]
│   │   └── service/
│   │       └── InterviewerServiceImpl.java    [NEW]
│   │
│   ├── organizationunit/
│   │   ├── model/
│   │   │   ├── OrganizationUnit.java          [NEW record]
│   │   │   └── OrganizationUnitType.java      [MOVE depuis api/domain/]
│   │   ├── port/
│   │   │   ├── serverside/
│   │   │   │   └── OrganizationUnitRepository.java [NEW]
│   │   │   └── userside/
│   │   │       └── OrganizationUnitService.java [NEW interface]
│   │   └── service/
│   │       └── OrganizationUnitServiceImpl.java [NEW]
│   │
│   ├── user/
│   │   ├── model/
│   │   │   └── User.java                      [NEW record]
│   │   ├── port/
│   │   │   ├── serverside/
│   │   │   │   └── UserRepository.java        [NEW]
│   │   │   └── userside/
│   │   │       ├── UserService.java           [NEW interface]
│   │   │       └── PreferenceService.java     [NEW interface]
│   │   └── service/
│   │       ├── UserServiceImpl.java           [NEW]
│   │       └── PreferenceServiceImpl.java     [NEW]
│   │
│   ├── message/
│   │   ├── model/
│   │   │   ├── Message.java                   [NEW record]
│   │   │   ├── MessageStatus.java             [NEW record]
│   │   │   └── MessageStatusType.java         [MOVE depuis api/domain/]
│   │   ├── port/
│   │   │   ├── serverside/
│   │   │   │   └── MessageRepository.java     [NEW]
│   │   │   └── userside/
│   │   │       └── MessageService.java        [NEW interface]
│   │   └── service/
│   │       └── MessageServiceImpl.java        [NEW]
│   │
│   ├── security/                              [EXISTS - inchange]
│   └── exception/                             [EXISTS - enrichir]
│
├── infrastructure/                            # Implementations des ports serverside
│   ├── campaign/
│   │   ├── adapter/
│   │   │   ├── CampaignDaoAdapter.java        [NEW]
│   │   │   ├── ReferentDaoAdapter.java        [NEW]
│   │   │   ├── CommunicationTemplateDaoAdapter.java [EXISTS]
│   │   │   └── VisibilityDaoAdapter.java      [EXISTS]
│   │   ├── entity/
│   │   │   ├── CampaignDB.java                [DONE - deplace depuis api/domain/Campaign.java]
│   │   │   ├── ReferentDB.java                [DONE - deplace depuis api/domain/Referent.java]
│   │   │   ├── CommunicationTemplateDB.java   [EXISTS]
│   │   │   ├── VisibilityDB.java              [EXISTS]
│   │   │   └── ...                            [EXISTS]
│   │   └── jpa/
│   │       ├── CampaignJpaRepository.java     [DONE - deplace depuis api/repository/CampaignRepository]
│   │       ├── ReferentJpaRepository.java     [DONE - deplace depuis api/repository/ReferentRepository]
│   │       └── ...                            [EXISTS]
│   │
│   ├── surveyunit/
│   │   ├── adapter/
│   │   │   ├── SurveyUnitDaoAdapter.java      [NEW]
│   │   │   ├── StateDaoAdapter.java           [NEW]
│   │   │   ├── AddressDaoAdapter.java         [NEW]
│   │   │   ├── ClosingCauseDaoAdapter.java    [NEW]
│   │   │   ├── ContactOutcomeDaoAdapter.java  [NEW]
│   │   │   ├── CommentDaoAdapter.java         [EXISTS]
│   │   │   └── CommunicationRequestDaoAdapter.java [EXISTS]
│   │   ├── entity/
│   │   │   ├── SurveyUnitDB.java              [RENAME api/domain/SurveyUnit.java]
│   │   │   ├── StateDB.java                   [RENAME api/domain/State.java]
│   │   │   ├── AddressDB.java                 [RENAME api/domain/Address.java + InseeAddress.java]
│   │   │   ├── SampleIdentifierDB.java        [RENAME api/domain/SampleIdentifier.java]
│   │   │   ├── ClosingCauseDB.java            [RENAME api/domain/ClosingCause.java]
│   │   │   ├── ContactAttemptDB.java          [RENAME api/domain/ContactAttempt.java]
│   │   │   ├── SurveyUnitTempZoneDB.java      [RENAME api/domain/SurveyUnitTempZone.java]
│   │   │   └── ...                            [EXISTS - CommentDB, ContactOutcomeDB, etc.]
│   │   └── jpa/
│   │       ├── SurveyUnitJpaRepository.java   [RENAME api/repository/SurveyUnitRepository]
│   │       ├── StateJpaRepository.java        [RENAME api/repository/StateRepository]
│   │       ├── AddressJpaRepository.java      [RENAME api/repository/AddressRepository]
│   │       └── ...
│   │
│   ├── interviewer/
│   │   ├── adapter/InterviewerDaoAdapter.java [NEW]
│   │   ├── entity/InterviewerDB.java          [RENAME api/domain/Interviewer.java]
│   │   └── jpa/InterviewerJpaRepository.java  [RENAME api/repository/InterviewerRepository]
│   │
│   ├── organizationunit/
│   │   ├── adapter/OrganizationUnitDaoAdapter.java [NEW]
│   │   ├── entity/OrganizationUnitDB.java     [RENAME api/domain/OrganizationUnit.java]
│   │   └── jpa/OrganizationUnitJpaRepository.java [RENAME]
│   │
│   ├── user/
│   │   ├── adapter/UserDaoAdapter.java        [NEW]
│   │   ├── entity/UserDB.java                 [RENAME api/domain/User.java]
│   │   └── jpa/UserJpaRepository.java         [RENAME]
│   │
│   ├── message/
│   │   ├── adapter/MessageDaoAdapter.java     [NEW]
│   │   ├── entity/
│   │   │   ├── MessageDB.java                 [RENAME]
│   │   │   ├── MessageStatusDB.java           [RENAME]
│   │   │   └── CampaignMessageRecipientDB.java [RENAME]
│   │   └── jpa/
│   │       ├── MessageJpaRepository.java      [RENAME]
│   │       └── MessageStatusJpaRepository.java [RENAME]
│   │
│   ├── security/                              [EXISTS - inchange]
│   └── mail/                                  [EXISTS - inchange]
│
├── api/                                       # Couche presentation : controllers + DTOs uniquement
│   ├── controller/                            [EXISTS - memes endpoints, imports mis a jour]
│   ├── campaign/
│   │   ├── controller/                        [EXISTS]
│   │   └── dto/                               [EXISTS - input/ et output/]
│   ├── surveyunit/
│   │   ├── controller/                        [EXISTS]
│   │   └── dto/                               [EXISTS]
│   ├── dto/                                   [EXISTS - DTOs restants progressivement reorganises]
│   ├── configuration/                         [EXISTS - CORS, logging, properties]
│   ├── web/                                   [EXISTS - exception handling, health check, validators]
│   ├── bussinessrules/                        [A TERME -> deplacer vers domain/]
│   ├── constants/                             [EXISTS]
│   └── exception/                             [EXISTS - exceptions API-level, domaine -> domain/exception/]
│
│   # SUPPRIME a terme :
│   # api/domain/         -> deplace vers infrastructure/*/entity/
│   # api/repository/     -> deplace vers infrastructure/*/jpa/
│   # api/service/impl/   -> logique metier extraite vers domain/*/service/
│   # api/service/        -> interfaces remplacees par domain/*/port/userside/
```

### Justifications de l'arborescence

1. **domain/ ne depend de RIEN** : seules les dependances Java standard, Lombok, et Spring annotations (@Service, @Transactional). Zero import vers `api/` ou `infrastructure/`.

2. **Modeles domaine = Java records** : immutables, sans annotations JPA. La logique metier (validation, merge) est dans le record (cf. `Visibility.merge()`).

3. **Ports serverside = interfaces** dans domain : definissent le contrat de persistence. Implementes par les adaptateurs dans infrastructure.

4. **Ports userside = interfaces** dans domain : definissent les operations metier. Implementes par les services domaine. Injectes dans les controllers.

5. **Entites DB suffixees `*DB`** : convention existante (`VisibilityDB`, `CommentDB`). Contiennent `toModel()` / `fromModel()` pour le mapping domaine <-> persistence.

6. **Repositories JPA suffixes `*JpaRepository`** : convention existante. Encapsules par les `*DaoAdapter`.

7. **Controllers restent dans api/** : la couche API est le point d'entree. Les controllers injectent les ports userside du domaine.

8. **DTOs restent dans api/** : ce sont des objets de la couche presentation. Ils utilisent `toModel()` / `fromModel()` pour convertir vers/depuis les modeles domaine.

---

## Strategie de resolution des dependances cross-agregat

**Principe** : un service domaine ne doit JAMAIS injecter un repository d'un autre agregat. Il injecte le **port userside** de l'autre agregat.

### Avant (violation) :
```java
// CampaignServiceImpl injecte directement des repos d'autres agregats
@Service
class CampaignServiceImpl {
    private final CampaignRepository campaignRepository;     // JPA direct
    private final UserRepository userRepository;             // AUTRE agregat, JPA direct
    private final SurveyUnitRepository surveyUnitRepository; // AUTRE agregat, JPA direct
}
```

### Apres (correct) :
```java
// CampaignServiceImpl injecte ses propres ports + les ports userside des autres agregats
@Service
class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;     // Port serverside OWN
    private final VisibilityService visibilityService;       // Port userside OWN
    private final UserService userService;                   // Port userside CROSS-AGREGAT
    private final OrganizationUnitService ouService;         // Port userside CROSS-AGREGAT
}
```

### Matrice de resolution :
| Service | Injecte actuellement | Doit injecter |
|---|---|---|
| CampaignServiceImpl | UserRepo, SURepo, OURepo, MessageRepo | UserService, SurveyUnitService, OUService |
| SurveyUnitServiceImpl | InterviewerRepo, CampaignRepo, OURepo | InterviewerService, CampaignService, OUService |
| InterviewerServiceImpl | VisibilityJpaRepo (VIOLATION!) | VisibilityService (port userside) |
| MessageServiceImpl | UserRepo, InterviewerRepo, CampaignRepo, OURepo | UserService, InterviewerService, CampaignService, OUService |
| StateServiceImpl | (via SurveyUnitService) | SurveyUnitService (port) |
| ContactOutcomeServiceImpl | (via SurveyUnitService) | SurveyUnitService (port) |

### Decomposition de UtilsService :
- `checkUserCampaignOUConstraints()` -> `UserService` (port userside)
- `getRelatedOrganizationUnits()` -> `OrganizationUnitService` (port userside)
- Appel REST externe (datacollection) -> port serverside dedie dans infrastructure

---

## Strategie DTO

1. **DTOs input/output** (requetes/reponses HTTP) : restent dans `api/` par agregat
2. **Mapping** : DTOs ont `toModel()` (input -> domaine) et `fromModel()` (domaine -> output)
3. **Queries JPQL retournant des DTOs** : remplacer par des projections infrastructure, l'adaptateur mappe vers un read model domaine
4. **Projections JPA** : `infrastructure/*/jpa/projection/` pour les requetes optimisees

---

## Plan de migration phase par phase

### Phase 0 : Fondation (prerequis) - DONE

- [x] Activer/creer les tests ArchUnit pour enforcer les regles d'architecture
- [x] Ajouter les regles en `@Disabled` comme objectif final
- [x] Regle `domainShouldNotDependOnInfrastructure` ciblee sur `fr.insee.pearljam.domain..` (pas `..domain..` qui matchait `api.domain`)
- [x] Regle `infrastructureClassesShouldNotBeAccessed` etendue pour accepter `api.campaign..` et `api.surveyunit..` temporairement

### Phase 1 : Completer l'agregat Campaign (deja 60% fait) - DONE

- [x] Creer `domain/campaign/model/Campaign.java` (record)
- [x] Creer `domain/campaign/model/Referent.java` (record)
- [x] Remplir `domain/campaign/port/serverside/CampaignRepository.java`
- [x] Deplacer `api/domain/Campaign.java` -> `infrastructure/campaign/entity/CampaignDB.java` (avec `@Entity(name="Campaign")` pour compatibilite JPQL)
- [x] Deplacer `api/domain/Referent.java` -> `infrastructure/campaign/entity/ReferentDB.java`
- [x] Deplacer `api/repository/CampaignRepository.java` -> `infrastructure/campaign/jpa/CampaignJpaRepository.java`
- [x] Deplacer `api/repository/ReferentRepository.java` -> `infrastructure/campaign/jpa/ReferentJpaRepository.java`
- [x] Mettre a jour `CampaignServiceImpl` pour utiliser `CampaignDB` en interne et `CampaignDB::toModel` aux frontieres
- [x] Mettre a jour tous les DTOs dependants (`CampaignResponseDto`, `CampaignSensitivityDto`, `ReferentDto`)
- [x] Mettre a jour toutes les entites JPA cross-references (`SurveyUnit`, `User`, `Message`, `CampaignMessageRecipient`)
- [x] Mettre a jour tous les services dependants (`StateServiceImpl`, `PreferenceServiceImpl`, `MessageServiceImpl`, `ReferentServiceImpl`, `UtilsServiceImpl`, `UserServiceImpl`, `ContactOutcomeServiceImpl`, `SurveyUnitServiceImpl`)
- [x] Mettre a jour tous les fichiers de test
- [x] Compilation OK, 199 tests unitaires passent (0 echecs)
- [ ] Creer `infrastructure/campaign/adapter/CampaignDaoAdapter.java` (defere - CampaignServiceImpl utilise encore CampaignJpaRepository directement)
- [ ] Extraire la logique metier de `api/service/impl/CampaignServiceImpl.java` vers `domain/campaign/service/CampaignServiceImpl.java` (defere - necessite Phase 2 d'abord pour les dependances cross-agregat)

#### Notes de Phase 1
- **Strategie intermediaire** : `CampaignServiceImpl` dans `api/` utilise `CampaignDB` (JPA entity) en interne et convertit via `CampaignDB::toModel` quand il retourne le modele domaine `Campaign`. L'extraction complete vers `domain/` est bloquee par les dependances cross-agregat (UserRepo, SURepo, OURepo, MessageRepo).
- **`@Entity(name="Campaign")`** sur `CampaignDB` preserve les queries JPQL existantes (`FROM Campaign camp`).
- Les anciens fichiers `api/domain/Campaign.java`, `api/domain/Referent.java`, `api/repository/CampaignRepository.java`, `api/repository/ReferentRepository.java` ont ete **supprimes**.

### Phase 2 : User + OrganizationUnit (debloque les dependances cross-agregat)
- [ ] Creer modeles domaine, ports, adaptateurs pour User et OrganizationUnit
- [ ] Decomposer UtilsService
- [ ] Migrer PreferenceService
- [ ] Mettre a jour tous les services qui injectent UserRepository ou OURepository

### Phase 3 : Interviewer
- [ ] Creer modele domaine, ports, adaptateur
- [ ] **Corriger la violation** : remplacer injection de VisibilityJpaRepository par VisibilityService (port)
- [ ] Mettre a jour les controllers

### Phase 4 : SurveyUnit (le plus gros agregat)
- [ ] Creer modeles domaine : SurveyUnit, State, Address, SampleIdentifier, ClosingCause, ContactAttempt
- [ ] Deplacer les enums (StateType, ClosingCauseType, ContactOutcomeType) vers domain/surveyunit/model/
- [ ] Definir tous les ports serverside
- [ ] Deplacer toutes les entites JPA de api/domain/ vers infrastructure/surveyunit/entity/
- [ ] Creer les adaptateurs
- [ ] Extraire logique metier de SurveyUnitServiceImpl, SurveyUnitUpdateServiceImpl
- [ ] Migrer StateService, ContactOutcomeService, ClosingCauseService

### Phase 5 : Message
- [ ] Creer modeles domaine, ports, adaptateurs
- [ ] Deplacer entites JPA vers infrastructure/message/entity/
- [ ] Extraire logique metier de MessageServiceImpl
- [ ] Gerer la dependance WebSocket via un port serverside (NotificationPort)

### Phase 6 : Nettoyage final
- [ ] Supprimer api/domain/ (vide)
- [ ] Supprimer api/repository/ (vide)
- [ ] Supprimer api/service/impl/ (vide - logique deplacee vers domain)
- [ ] Consolider les interfaces api/service/ (remplacees par ports userside)
- [ ] Deplacer BusinessRules.java vers le package domaine approprie
- [ ] Activer TOUS les tests ArchUnit
- [ ] Deplacer les enums restants vers leurs agregats domaine
- [ ] Finaliser le CampaignDaoAdapter et l'extraction de CampaignServiceImpl vers domain/

---

## Tests ArchUnit cibles

```java
// Regle 1 : Le domaine ne depend JAMAIS de api/ ou infrastructure/
noClasses().that().resideInAPackage("fr.insee.pearljam.domain..")
    .should().dependOnClassesThat()
    .resideInAnyPackage("..api..", "..infrastructure..");

// Regle 2 : Les adaptateurs implementent des ports domaine
classes().that().resideInAPackage("..infrastructure..adapter..")
    .should().implement(predicateForDomainPorts());

// Regle 3 : Pas d'annotations JPA dans le domaine
noClasses().that().resideInAPackage("..domain..")
    .should().beAnnotatedWith(Entity.class);

// Regle 4 : Les controllers ne dependent que des ports userside et des DTOs
classes().that().resideInAPackage("..api..controller..")
    .should().onlyDependOnClassesThat()
    .resideInAnyPackage("..api..", "..domain..port.userside..", "..domain..model..", "..domain..exception..", ...);
```

### Tests ArchUnit actifs (Phase 0)

| Test | Statut | Description |
|---|---|---|
| `serviceClassesShouldNotBeAccessedDirectly` | ACTIF | Les services domaine ne sont pas accedes directement |
| `usersidePortsShouldOnlyBeAccessedByControllerAndServices` | ACTIF | Les ports userside ne sont accedes que par controllers et services |
| `serversidePortsShouldOnlyBeAccessedByDaoAndServices` | ACTIF | Les ports serverside ne sont accedes que par adapters et services |
| `modelsShouldBeAccessedByAllLayers` | ACTIF | Les modeles sont accessibles partout |
| `serviceClassesShouldOnlyAccessDomainClasses` | ACTIF | Les services domaine n'accedent qu'aux classes domaine |
| `infrastructureClassesShouldNotBeAccessed` | ACTIF | Infrastructure accedee uniquement par infra, api.dto, api.domain, api.service, api.campaign, api.surveyunit |
| `infrastructureSecurityClassesShouldOnlyBeAccessedByConfigurationApi` | ACTIF | Security infra isolee |
| `domainShouldNotDependOnInfrastructure` | ACTIF | `fr.insee.pearljam.domain..` ne depend pas de infrastructure |
| `presentationLayerShouldNotBeAccessedByOtherLayers` | @Disabled | Active apres refacto complet |
| `domainShouldNotDependOnApi` | @Disabled | Domain depend encore de api.domain enums |
| `infrastructureShouldNotDependOnApi` | @Disabled | Infrastructure reference encore api.domain entities |

---

## Verification

1. **Compilation** : `mvn compile` apres chaque phase
2. **Tests existants** : `mvn test` - tous les tests doivent continuer a passer
3. **Tests ArchUnit** : progressivement actives phase par phase
4. **API REST** : aucun endpoint ne change (verification via tests d'integration existants)
5. **Tests Cucumber/BDD** : doivent continuer a passer (tests boite noire)
6. **Tests Docker-dependants** : TestAuthKeyCloak, TestNoAuth, VisibilityDaoAdapterTest, CommentDaoAdapterTest, integration/* necessitent Docker

---

## Fichiers critiques a modifier

| Fichier | Raison | Statut |
|---|---|---|
| `api/service/impl/CampaignServiceImpl.java` | Plus gros service, plus de dependances cross-agregat | PARTIELLEMENT MIGRE |
| `api/service/impl/SurveyUnitServiceImpl.java` | Plus complexe, 7 repos injectes | Phase 4 |
| `api/service/impl/InterviewerServiceImpl.java` | Violation cross-layer a corriger | Phase 3 |
| `api/domain/SurveyUnit.java` | Entite JPA la plus complexe, logique metier embarquee | Phase 4 |
| `infrastructure/campaign/entity/CampaignDB.java` | Entite JPA Campaign migree | DONE |
| `infrastructure/campaign/jpa/CampaignJpaRepository.java` | JPA repository Campaign migre | DONE |
| `domain/campaign/model/Campaign.java` | Record domaine Campaign | DONE |
| `domain/campaign/port/serverside/CampaignRepository.java` | Interface port remplie | DONE |
| `infrastructure/campaign/adapter/VisibilityDaoAdapter.java` | Pattern de reference a reproduire | EXISTS |
| `domain/campaign/service/VisibilityServiceImpl.java` | Pattern de reference pour les services domaine | EXISTS |

---

## Risques et mitigations

1. **Queries JPQL retournant des DTOs api** (`new CampaignDto(...)`) : remplacer par des projections infrastructure + mapping dans l'adaptateur
2. **Entites JPA avec references croisees** (`VisibilityDB` reference `CampaignDB`) : OK car toutes les entites DB sont dans infrastructure, elles peuvent se referencer entre elles
3. **SurveyUnit JPA avec logique metier** : splitter la logique pure vers le record domaine, la logique persistence vers l'entite DB
4. **Tests** : les tests mirrorent la structure source, ils doivent etre deplaces en parallele
5. **`@Entity(name="...")` obligatoire** : quand on renomme une entite JPA (ex: `Campaign` -> `CampaignDB`), il faut `@Entity(name="Campaign")` pour que les JPQL existantes (`FROM Campaign camp`) continuent a fonctionner
