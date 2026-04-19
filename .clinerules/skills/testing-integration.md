# Skill : Testing — Tests d'Intégration & Architecture

## Objectif

Ce document définit les standards des **tests qui touchent l'infrastructure
réelle** : adaptateurs (JPA / JDBC), tests d'intégration `@SpringBootTest`,
mappers domain ↔ entity, scénarios Cucumber, et tests d'architecture
(ArchUnit). Il couvre les ~30% des tests restants.

Pour la stratégie Fake vs Mock, les services Domain et les tests de
contrôleur MockMvc `standaloneSetup`, voir **`testing.md`**.

## Stack

- **Spring Boot Test** (`@SpringBootTest`) avec `WebEnvironment.RANDOM_PORT`
- **`@Sql`** : jeu de données par test (`ExecutionPhase.BEFORE_TEST_METHOD` /
  `AFTER_TEST_METHOD`)
- **`@Transactional`** : rollback automatique entre tests
- **`@ActiveProfiles({"auth", "test"})`** : active OIDC + H2/Liquibase de test
- **`FixedDateServiceConfiguration`** : `@Import` pour figer le temps
- **Cucumber** : `io.cucumber.spring.CucumberContextConfiguration`, glue
  `fr.insee.pearljam.features`
- **ArchUnit** : `com.tngtech.archunit` — règles de dépendances entre modules

---

## Quand écrire un test d'intégration ?

| Besoin | Test à écrire |
|---|---|
| Valider une requête JPQL / JDBC (résultat réel sur schéma Liquibase) | IT Adaptateur |
| Valider un mapping Entity ↔ Domain bidirectionnel | Test de mapper (unit ou IT) |
| Valider un endpoint bout-en-bout (HTTP → DB → HTTP) | IT `@SpringBootTest` + MockMvc |
| Valider les codes 401/403 réels (chaîne Spring Security) | IT `@SpringBootTest` (voir `context/security.md`) |
| Valider un scénario métier décrit en Gherkin | Cucumber |
| Valider les frontières entre modules (hexagonal) | ArchUnit |

**Ne pas écrire** un IT pour un service Domain pur (pas de DB, pas de HTTP) :
test unitaire avec Fake/Mock dans `testing.md`.

---

## Tests d'Adaptateur (persistence)

Les adaptateurs `...DaoAdapter` encapsulent les requêtes aggregées (comptages,
pagination, stats). Les requêtes `CRUD` passent par `JpaRepository` et sont
validées par les IT de contrôleur.

### Convention

- Nom du fichier : `[Adaptateur]IT.java` dans
  `pearljam-api/src/test/java/fr/insee/pearljam/integration/...`
- Annotations : `@SpringBootTest`, `@ActiveProfiles({"auth", "test"})`,
  `@Transactional`, `@Sql` pour le jeu de données.
- Jeu de données : fichier SQL dans `src/test/resources/db/` (cohérent avec
  `ScriptConstants`).
- Assertions : **AssertJ exclusivement**.

### Exemple

```java
@ActiveProfiles({"auth", "test"})
@SpringBootTest
@Transactional
@Import(FixedDateServiceConfiguration.class)
class VisibilityDaoAdapterIT {

    @Autowired
    private VisibilityDaoAdapter adapter;

    @Test
    @DisplayName("Should return visibilities for campaign with multiple OUs")
    @Sql(scripts = "/dataset/visibilities-multi-ou.sql")
    void shouldReturnVisibilitiesForCampaignWithMultipleOUs() {
        var visibilities = adapter.findVisibilities("simpsons2020x00");

        assertThat(visibilities)
            .hasSize(3)
            .extracting(Visibility::organizationalUnitId)
            .containsExactlyInAnyOrder("OU-NORTH", "OU-SOUTH", "OU-EAST");
    }
}
```

---

## Tests d'Intégration de Contrôleur (end-to-end)

Pour valider une route HTTP avec le contexte Spring complet : sécurité OIDC,
filtres, persistance réelle, mappers, `ExceptionControllerAdvice`.

```java
@ActiveProfiles({"auth", "test"})
@AutoConfigureMockMvc
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Import(FixedDateServiceConfiguration.class)
class CampaignIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CampaignJpaRepository campaignRepository;

    @Test
    @DisplayName("Should retrieve on-going campaigns for admin")
    void shouldRetrieveOnGoingCampaignsForAdmin() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                get(Constants.API_CAMPAIGNS_ON_GOING)
                    .with(authentication(AuthenticatedUserTestHelper.AUTH_ADMIN))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        JSONAssert.assertEquals(
            expectedJson,
            mvcResult.getResponse().getContentAsString(),
            JSONCompareMode.LENIENT);
    }
}
```

**Différences avec un test `standaloneSetup`** :

- La chaîne Spring Security est active → 401/403 réels testables.
- Le mapping Entity ↔ Domain est exercé → valide la cohérence.
- La transaction rollback garantit l'isolation des tests.

Scénarios 401/403 : voir **`context/security.md`** pour l'usage de
`AuthenticatedUserTestHelper` (AUTH_ADMIN, AUTH_INTERVIEWER, AUTH_LOCAL_USER,
AUTH_NATIONAL_USER, AUTH_WEBCLIENT) et `@WithMockUser`.

---

## Tests de Mapper Domain ↔ Entity

Les mappers JPA (entity → domain et domain → entity) sont pure fonctions sans
dépendance Spring : test **unitaire** classique, pas d'`@SpringBootTest`.

```java
class CampaignEntityMapperTest {

    @Test
    @DisplayName("Should map CampaignDB to Campaign with all fields")
    void shouldMapCampaignDBToCampaign() {
        var entity = new CampaignDB(
            "simpsons2020x00",
            "Simpsons",
            CampaignIdentificationConfiguration.IASCO,
            ...);

        Campaign campaign = CampaignEntityMapper.toModel(entity);

        assertThat(campaign.id()).isEqualTo("simpsons2020x00");
        assertThat(campaign.identificationConfiguration())
            .isEqualTo(CampaignIdentificationConfiguration.IASCO);
    }

    @Test
    @DisplayName("Should round-trip Campaign via entity without loss")
    void shouldRoundTripCampaign() {
        var original = new Campaign("c1", "Test", ...);

        var roundTrip = CampaignEntityMapper.toModel(
            CampaignEntityMapper.toEntity(original));

        assertThat(roundTrip).isEqualTo(original);
    }
}
```

**Règle** : tout mapper doit avoir un test round-trip (domain → entity →
domain) pour détecter les pertes de champ lors des évolutions.

---

## Tests Cucumber

Les scénarios Gherkin vivent dans `pearljam-api/src/test/resources/features/`.
Le runner unique est `CucumberTestRunner` (mutualise le contexte Spring).

### Structure

```
pearljam-api/src/test/
├── java/fr/insee/pearljam/features/
│   ├── CucumberTestRunner.java              # Suite JUnit 5 + Cucumber
│   └── [Feature]Stepdefs.java               # Glue Java (given/when/then)
└── resources/features/
    └── identification.feature                # Scénarios Gherkin
```

### Runner partagé

```java
@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles({"auth", "test"})
@AutoConfigureMockMvc
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "fr.insee.pearljam.features")
public class CucumberTestRunner {
}
```

### Quand écrire un Cucumber plutôt qu'un IT ?

| Cas | Préférer |
|---|---|
| Logique métier stabilisée, documentation pour le métier | Cucumber |
| Transitions d'état complexes (machine à états) | Cucumber |
| Test d'endpoint CRUD classique | IT `@SpringBootTest` |
| Cas d'erreur / codes HTTP spécifiques | IT `@SpringBootTest` |

**Règle** : un scénario Cucumber doit être lisible par un non-développeur. Si
le `Then` dépend d'un détail d'implémentation, rebasculer en IT Java.

---

## Tests d'Architecture (ArchUnit)

Centralisés dans
`pearljam-api/src/test/java/fr/insee/pearljam/config/ModuleBoundariesArchTests.java`.
Ces tests tournent à chaque build et empêchent les régressions sur les
frontières hexagonales.

### Règles en place

| Règle | Statut |
|---|---|
| API → JPA repositories (`org.springframework.data.jpa.repository`) | Interdit |
| API → `jakarta.persistence` | Interdit |
| Domain → API | Interdit |
| Domain → Infrastructure (sauf entities JPA) | Toléré temporairement |
| Contracts → API ou Infrastructure | Interdit |
| Infrastructure → API | Interdit |

### Ajouter une règle

```java
@Test
void domainShouldNotDependOnApiPackages() {
    noClasses()
        .that().resideInAPackage("fr.insee.pearljam.domain..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "fr.insee.pearljam.api.."
        )
        .check(importedClasses);
}
```

**Ne jamais** désactiver une règle ArchUnit pour débloquer un build. Refactorer
le code fautif ou, si l'exception est légitime, documenter précisément le
pourquoi dans le test.

Référence des frontières : **`context/pearljam-arch-state.md`**.

---

## Profils et Configuration

| Profil | Usage | Effet |
|---|---|---|
| `auth` | IT standard | Active Spring Security OIDC (Keycloak mocké) |
| `noauth` | Tests sans auth | Désactive la chaîne Security |
| `demo` | `DemoDataIT` | Active les données de démo |
| `test` | Toujours combiné | Config H2 + Liquibase de test |

La config de test vit dans `pearljam-api/src/test/resources/application*.properties`.
Les scripts SQL sont sous `src/test/resources/db/` et `src/test/resources/dataset/`.

---

## Conventions (rappel)

- **Nommage** : `[Entity]IT` pour `@SpringBootTest`, `[Mapper]Test` pour les
  mappers, `[Feature]Stepdefs` pour Cucumber.
- **Assertions** : AssertJ + `JSONAssert` pour les réponses complexes.
- **Isolation** : `@Transactional` + `@Sql` sur chaque test qui touche la DB.
- **Temps** : `@Import(FixedDateServiceConfiguration.class)` pour figer
  `DateService`.
- **Auth** : `.with(authentication(AuthenticatedUserTestHelper.AUTH_XXX))`
  sur `MockMvc` — voir `context/security.md`.
- **Pas de** : dépendance entre tests, ordre implicite, état partagé via
  fichier ou BDD externe.

## Voir aussi

- `testing.md` — stratégie Fake/Mock, organisation des tests, contrôleurs MockMvc
- `context/security.md` — scénarios 401/403, `AuthenticatedUserTestHelper`
- `context/pearljam-arch-state.md` — principes hexagonaux testés par ArchUnit
- `context/project-context.md` — profils Spring, Liquibase, Docker Compose
