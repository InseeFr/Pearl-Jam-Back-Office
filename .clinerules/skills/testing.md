# Skill : Testing — Pearl Jam Back Office

## Objectif

Ce document définit les standards de tests **cibles** du projet.
Il prescrit une stratégie unifiée de doublures de test et distingue le legacy à ne pas reproduire.

## Stack de Test

- **JUnit 5** + **AssertJ** (exclusivement — jamais JUnit `assertEquals`)
- **Mockito** : usage restreint (voir règles ci-dessous)
- **MockMvc** : tests de contrôleurs (standaloneSetup)
- **JSONAssert** : comparaison JSON
- **Spring Boot Test** : tests d'intégration (`@SpringBootTest`)
- **Cucumber** : tests BDD
- **ArchUnit** : frontières de modules

---

## Legacy vs Cible

Le projet est en migration. **Ne jamais reproduire le legacy dans du nouveau code.**

| Aspect | Legacy | Cible |
|---|---|---|
| 404 dans le contrôleur | `if (result == null) return NOT_FOUND` | Exception métier + `ExceptionControllerAdvice` |
| Tests contrôleur | Mockito + `ResponseEntity` direct | Fake + MockMvc + `apiErrorMatches()` |
| Assertions | JUnit `assertEquals`, `assertNull` | AssertJ `assertThat()`, `assertThatThrownBy()` |
| Nommage tests | `testGetCampaign01()` | `shouldReturnCampaignWhenExists()` |
| Port domain | `Optional<CampaignDB>` (fuite JPA) | `Optional<Campaign>` (modèle domain) |
| Retour service | `null` pour "pas trouvé" | Exception métier |
| Packages doublures | `dummy/` et `stub/` mélangés | `fake/` uniquement |

---

## Stratégie de Doublures : Fake ou Mock ?

### Le critère : la taille du port

Le vrai problème des Fakes sur ce projet : les gros ports.
`CampaignRepository` a **28 méthodes**, `CampaignService` en a **25**.
Écrire un Fake pour ça = implémenter 25 méthodes dont 80% retournent `List.of()`
ou `throw new UnsupportedOperationException()`. Et chaque évolution du port
casse tous les Fakes à la compilation.

À l'inverse, `VisibilityRepository` a **5 méthodes**, `CommentRepository` en a **1**.
Un Fake pour ça coûte 10 lignes, ne cassera presque jamais, et documente le contrat.

### La règle

| Quoi tester | Taille du port | Doublure | Pourquoi |
|---|---|---|---|
| Service via port **≤ 6 méthodes** | Petit | **Fake** | Lisible, stable, documente le contrat |
| Service via port **> 6 méthodes** | Gros | **Mockito** | Évite 20+ méthodes vides, résiste aux évolutions du port |
| Contrôleur via port entrant | Variable | **Fake** | Teste le vrai comportement HTTP (MockMvc) |
| Adaptateur (persistence) | — | **Test d'intégration** | Tester la vraie requête SQL, pas du câblage |
| Date/temps | 1 méthode | **FixedDateService** | Fake du port `DateService` |

Le seuil de 6 est une heuristique : au-delà, le ratio "méthodes utilisées / méthodes
à implémenter" devient trop faible pour justifier un Fake.

### Arbre de décision

```
Je teste un ADAPTATEUR (persistence/http) ?
  └─ OUI → Test d'intégration (@SpringBootTest + vraie DB)
  └─ NON → Je teste un SERVICE ou un CONTRÔLEUR
       └─ Le port a-t-il ≤ 6 méthodes ?
            └─ OUI → Fake (package fake/)
            └─ NON → Mockito (mock + when)
                 └─ Ai-je besoin de verify ?
                      └─ OUI (paramètres métier significatifs) → verify
                      └─ NON → juste when/thenReturn
```

### Exemples concrets du projet

| Port | Méthodes | Doublure recommandée |
|---|---|---|
| `CommentRepository` | 1 | Fake |
| `VisibilityRepository` | 5 | Fake |
| `DateService` | 1 | Fake (`FixedDateService`) |
| `VisibilityService` | ~10 | Fake pour contrôleur (flags), Mockito sinon |
| `StateRepository` | 19 | Mockito |
| `CampaignRepository` | 28 | Mockito |
| `CampaignService` | 25 | Fake pour contrôleur (flags), Mockito sinon |
| `SurveyUnitRepository` | 32 | Mockito |
| `VisibilityDaoAdapter` | — | Test d'intégration |

**Exception contrôleurs** : pour les tests de contrôleur, on utilise toujours un Fake
du port entrant (même gros), car le pattern flags/getters reste plus expressif que Mockito
pour tester les codes HTTP et les états après action.

---

## Conventions des Fakes

### Package et nommage

```
src/test/java/.../fake/
  └─ [NomDuPort]Fake.java

Exemples :
  VisibilityRepositoryFake.java
  CampaignServiceFake.java
  AuthenticatedUserServiceFake.java
```

### Fake de port sortant (repository) — in-memory

```java
package fr.insee.pearljam.domain.campaign.service.fake;

public class VisibilityRepositoryFake implements VisibilityRepository {

    private final List<Visibility> visibilities = new ArrayList<>();

    // Méthode de test (pas dans le port)
    public void save(Visibility visibility) {
        if (!visibilities.contains(visibility)) {
            visibilities.add(visibility);
        }
    }

    @Override
    public Optional<Visibility> findVisibility(String campaignId, String ouId) {
        return visibilities.stream()
                .filter(v -> v.campaignId().equals(campaignId))
                .filter(v -> v.organizationalUnitId().equals(ouId))
                .findFirst();
    }

    @Override
    public void updateDates(Visibility update) throws VisibilityNotFoundException {
        Visibility toRemove = findVisibility(update.campaignId(), update.organizationalUnitId())
            .orElseThrow(VisibilityNotFoundException::new);
        visibilities.remove(toRemove);
        visibilities.add(update);
    }
}
```

### Fake de port entrant (service) — flags + getters

Pour les tests de contrôleur. Les flags simulent les exceptions,
les getters permettent de vérifier l'état après action.

```java
package fr.insee.pearljam.api.campaign.controller.fake;

@RequiredArgsConstructor
public class CampaignServiceFake implements CampaignService {

    @Getter private boolean deleted = false;
    @Getter private boolean deleteForced = false;
    @Setter private boolean shouldThrowCampaignNotFoundException = false;
    @Getter private CampaignCreateDto campaignCreated = null;

    @Override
    public void delete(String id, boolean force) throws CampaignNotFoundException {
        deleteForced = force;
        if (shouldThrowCampaignNotFoundException) throw new CampaignNotFoundException();
        deleted = true;
    }

    // Méthodes non testées dans CE test → signal clair
    @Override
    public List<CampaignDto> getAllCampaigns() {
        throw new UnsupportedOperationException("Not used in this test");
    }
}
```

### Gestion du temps — FixedDateService

Le projet utilise un port `DateService` (1 méthode) injecté dans les services
domaine. Son Fake est `FixedDateService` avec un timestamp constant.

```java
// Port (domain)
public interface DateService {
    long getCurrentTimestamp();
}

// Fake (test)
public class FixedDateService implements DateService {
    public static final long FIXED_TIMESTAMP = 1735689600000L;

    @Override
    public long getCurrentTimestamp() {
        return FIXED_TIMESTAMP;
    }
}

// Utilisation dans un test
@BeforeEach
void setUp() {
    dateService = new FixedDateService();
    service = new SurveyUnitUpdateServiceImpl(..., dateService);
}
```

### Migration progressive

- **Ne pas renommer** les `dummy/` et `stub/` existants tant qu'on ne modifie pas le test
- **Nouveau code** : utiliser `fake/` et les conventions ci-dessus
- **Quand on modifie un test existant** : en profiter pour migrer vers `fake/`

---

## Tests par Couche

### 1. Tests de Service Domain — Fake (port petit)

```java
class VisibilityServiceImplTest {

    private VisibilityRepositoryFake repository;
    private VisibilityServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = new VisibilityRepositoryFake();
        service = new VisibilityServiceImpl(repository);
    }

    @Test
    @DisplayName("Should return visibilities for valid campaign id")
    void shouldReturnVisibilitiesForValidCampaignId() {
        // Given
        repository.save(visibility1);
        repository.save(visibility3);

        // When
        var result = service.findVisibilities("campaign1");

        // Then
        assertThat(result)
            .hasSize(2)
            .containsExactlyInAnyOrder(visibility1, visibility3);
    }

    @Test
    @DisplayName("Should throw VisibilityNotFoundException when updating non-existent")
    void shouldThrowWhenUpdatingNonExistent() {
        var unknown = new Visibility("invalid", "invalid", ...);

        assertThatThrownBy(() -> service.updateVisibility(unknown))
            .isInstanceOf(VisibilityNotFoundException.class)
            .hasMessage(VisibilityNotFoundException.MESSAGE);
    }
}
```

### 2. Tests de Service Domain — Mockito (port gros)

```java
class CampaignReportingServiceTest {

    static final LocalDate FIXED_TODAY = LocalDate.of(2025, 6, 15);

    CampaignRepository campaignRepository;          // 28 méthodes → mock
    CampaignDailyStatsRepositoryPort statsRepository; // 13 méthodes → mock
    UserService userService;                         // 22 méthodes → mock
    CampaignReportingService service;

    @BeforeEach
    void setup() {
        campaignRepository = mock(CampaignRepository.class);
        statsRepository = mock(CampaignDailyStatsRepositoryPort.class);
        userService = mock(UserService.class);
        service = new CampaignReportingService(
            campaignRepository, statsRepository, userService, ...);

        // Setup par défaut — on ne configure que ce qu'on utilise
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of(OU));
        when(campaignRepository.findAllManagedAndNotClosedCampaignsByOuIds(anyList(), any()))
            .thenReturn(List.of());
    }

    @Test
    @DisplayName("Should return empty list when user has no organization units")
    void shouldReturnEmptyListWhenNoOUs() {
        when(userService.getUserOUsModel(USER_ID, true)).thenReturn(List.of());

        var result = service.getCampaignsStats(USER_ID, FIXED_TODAY, stats -> stats);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should call repository with correct interviewer id")
    void shouldCallRepositoryWithCorrectInterviewerId() {
        // ... setup ...

        service.getCampaignsStatsForInterviewer(USER_ID, FIXED_TODAY, "itw-123", stats -> stats);

        // verify justifié : on vérifie un paramètre métier significatif
        verify(statsRepository).getCampaignsStatsForInterviewer(
            eq("itw-123"), anyList(), anyList(), eq(FIXED_TODAY));
    }
}
```

### 3. Tests de Contrôleur (MockMvc + Fake)

Toujours Fake (même pour les gros ports), car le pattern flags/getters
est plus expressif que Mockito pour les tests HTTP.

```java
class CampaignControllerTest {

    private MockMvc mockMvc;
    private CampaignServiceFake campaignService;

    @BeforeEach
    void setup() {
        campaignService = new CampaignServiceFake();
        var authService = new AuthenticatedUserServiceFake(AuthenticatedUserTestHelper.AUTH_ADMIN);
        var controller = new CampaignController(campaignService, ..., authService, true);
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
            .build();
    }

    @Test
    @DisplayName("Should return not found when campaign not found")
    void shouldReturnNotFoundWhenCampaignNotFound() throws Exception {
        campaignService.setShouldThrowCampaignNotFoundException(true);

        mockMvc.perform(get("/api/campaign/id").contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcTestUtils.apiErrorMatches(
                HttpStatus.NOT_FOUND, "/api/campaign/id",
                CampaignNotFoundException.MESSAGE));
    }

    @Test
    @DisplayName("Should delete campaign with force flag")
    void shouldDeleteWithForce() throws Exception {
        mockMvc.perform(delete("/api/campaign/id")
                .param("force", "true")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // Vérifier l'état du Fake — pas de verify Mockito
        assertThat(campaignService.isDeleteForced()).isTrue();
        assertThat(campaignService.isDeleted()).isTrue();
    }
}
```

**Utilitaires partagés** (toujours réutiliser, ne pas réinventer) :

| Classe | Usage |
|---|---|
| `MockMvcTestUtils.apiErrorMatches(status, path, msg)` | Vérifie structure d'erreur API |
| `MockMvcTestUtils.createExceptionControllerAdvice()` | `ExceptionControllerAdvice` partagé |
| `JsonTestHelper.toJson(object)` | Sérialise pour comparaison JSON |
| `AuthenticatedUserTestHelper.AUTH_ADMIN` | Token admin pré-configuré |

### 4. Tests d'Adaptateur (intégration uniquement)

Un adaptateur transforme des appels JPA/JDBC en modèles domain.
Le mocker ou le faker ne vérifierait rien d'utile.
**Seul un test d'intégration avec une vraie DB est pertinent.**

```java
@ActiveProfiles(profiles = {"auth", "test"})
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class VisibilityIT {

    @Autowired private MockMvc mockMvc;

    @Test
    void testGetVisibilities() throws Exception {
        var result = mockMvc.perform(
                get("/api/campaign/SIMPSONS2020X00/visibilities")
                    .with(authentication(AuthenticatedUserTestHelper.AUTH_ADMIN))
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        JSONAssert.assertEquals(expected,
            result.getResponse().getContentAsString(),
            JSONCompareMode.NON_EXTENSIBLE);
    }
}
```

### 5. Tests Paramétrés

```java
@ParameterizedTest
@MethodSource("provideIdentificationScenarios")
@DisplayName("Should compute identification state correctly")
void shouldComputeIdentificationState(Identification id,
                                       IdentificationConfiguration config,
                                       IdentificationState expected) {
    assertThat(IdentificationState.getState(id, config)).isEqualTo(expected);
}
```

### 6. Tests d'Architecture (ArchUnit)

Règles dans `ModuleBoundariesArchTests.java` :

| Règle | Statut |
|---|---|
| API → JPA repositories | Interdit |
| Domain → API | Interdit |
| Domain → Infrastructure (sauf entities) | Toléré temporairement |
| Contracts → API ou Infrastructure | Interdit |
| Infrastructure → API | Interdit |

---

## Conventions (nouveau code)

- **Nommage** : `shouldXxxWhenYyy` + `@DisplayName`
- **Assertions** : AssertJ exclusivement
- **Pattern** : Given/When/Then
- **Doublures** : Fake dans `fake/` pour ports ≤ 6 méthodes, Mockito sinon
- **Contrôleurs** : toujours Fake (flags/getters) + MockMvc
- **Adaptateurs** : test d'intégration uniquement
- **Temps** : `FixedDateService` (Fake du port `DateService`)
- **Pas de** : `@Disabled`, `@Ignore`, `@Order`, état partagé entre tests

## Couverture — Checklist par Feature

- [ ] Cas nominal (happy path)
- [ ] Chaque exception métier → `assertThatThrownBy`
- [ ] Cas limites : null, empty, liste vide
- [ ] Branches conditionnelles
- [ ] Codes HTTP : 200, 400, 404, 409
- [ ] JSON comparé avec JSONAssert pour réponses complexes
- [ ] État du Fake vérifié après action
- [ ] Ne pas toucher aux tests legacy sauf refactoring explicite