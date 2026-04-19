# Skill : Sécurité — Pearl Jam Back Office

## Objectif

Ce document couvre l'authentification OIDC, l'autorisation par rôle, la
configuration Spring Security 7, les utilitaires de test d'auth, et les
patterns à suivre côté API.

Il sert de référence pour LeCodeur, LeTesteur et LeRefactoAnalyste quand ils
touchent à un endpoint, un `SecurityFilterChain`, ou un test de contrôleur.

## Stack

| Composant | Version | Notes                                             |
|---|---|---------------------------------------------------|
| Spring Security | 7 | via Spring Boot 4.0.x                             |
| Protocole | OAuth2 Resource Server + JWT | bearer token                                      |
| Fournisseur d'identité (local) | Keycloak | via `compose.yml`                                 |
| Profils de test | `auth` (Keycloak) / `noauth` | `application-auth.yml` / `application-noauth.yml` |
| Module | `pearljam-infrastructure-security` | config Spring Security                            |

## Modèle de rôles

Les rôles métiers sont définis dans l'enum domain :

```java
// fr.insee.pearljam.domain.security.model.AuthorityRole
public enum AuthorityRole {
    ADMIN, INTERVIEWER, LOCAL_USER, NATIONAL_USER, WEBCLIENT;

    public static final String ROLE_PREFIX = "ROLE_";

    public String securityRole() { return ROLE_PREFIX + name(); }
}
```

Mapping JWT → rôle appliqué par `GrantedAuthorityConverter`
(infrastructure-security). Les noms JWT (`interviewer`, `localUser`,
`nationalUser`, `admin`, `webclient`) sont configurables via
`RoleProperties` (propriétés `feature.oidc.role.*`).

## Configuration Spring Security (cible)

Deux `SecurityFilterChain` selon le profil :

- `OidcSecurityConfiguration` (profil par défaut) — JWT resource server.
- `NoAuthSecurityConfiguration` (profil `noauth`) — `permitAll` pour dev/tests.

Règles clés de la chaîne OIDC :

```java
http
    .securityMatcher("/**")
    // API stateless : CSRF désactivé intentionnellement (SonarJava:S4502)
    .csrf(AbstractHttpConfigurer::disable)
    .cors(Customizer.withDefaults())
    .headers(h -> h
        .xssProtection(x -> x.headerValue(DISABLED))
        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'none'"))
        .referrerPolicy(r -> r.policy(SAME_ORIGIN)))
    .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
    .oauth2ResourceServer(o -> o
        .accessDeniedHandler(customAccessDeniedHandler)
        .authenticationEntryPoint(customAuthenticationEntrypoint)
        .jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));
```

**Invariants à préserver** :
- `SessionCreationPolicy.STATELESS` (pas de session HTTP).
- CSRF désactivé **uniquement** parce que l'API est stateless bearer-token.
  Si un cookie session est réintroduit, CSRF **doit** être réactivé.
- Aucune exception `permitAll()` en dehors de `/healthcheck` et `OPTIONS`.

## Pattern d'autorisation (cible projet)

Le projet utilise **deux mécanismes complémentaires** :

1. **Matchers centralisés** dans `OidcSecurityConfiguration.authorizeRequests()` :

   ```java
   http.authorizeHttpRequests(cfg -> cfg
       .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
       .requestMatchers(HttpMethod.GET, API_SURVEYUNITS)
           .hasAnyRole(adminRole, interviewerRole, localUserRole, nationalUserRole)
       .requestMatchers(HttpMethod.POST, API_SURVEYUNITS)
           .hasAnyRole(adminRole, webclientRole)
       .anyRequest().authenticated());
   ```

2. **Test de rôle dans le contrôleur** via `AuthenticatedUserService` quand
   la logique dépend du rôle (filtrage de données, pas juste accès) :

   ```java
   // SurveyUnitController.java:317
   if (authenticatedUserService.hasRole(AuthorityRole.ADMIN)) {
       return allSurveyUnits();
   }
   if (authenticatedUserService.hasRole(AuthorityRole.INTERVIEWER)) {
       return surveyUnitsFor(authenticatedUserService.currentUserId());
   }
   ```

**À ne pas utiliser** :
- `@PreAuthorize("hasRole(...)")` — le projet n'en utilise pas. Si on en
  introduit, il faut activer `@EnableMethodSecurity` et justifier.
- Vérification manuelle de rôle via `SecurityContextHolder.getContext()` —
  toujours passer par `AuthenticatedUserService` (port injecté, testable).

## Exceptions & mapping HTTP

Le domaine lève des exceptions métier ; le mapping HTTP est assuré par
`ExceptionControllerAdvice` (`pearljam-api`) :

| Exception | HTTP | Source |
|---|---|---|
| `AccessDeniedException` (Spring Security) | 403 | auth |
| `*NotFoundException` (métier, par bounded context) | 404 | métier |
| `*AlreadyExistException` | 409 | métier |
| `HttpMessageNotReadableException` | 400 | parsing |
| `AuthenticationException` | 401 | auth |

Règle : une exception d'auth (`AccessDeniedException`,
`AuthenticationException`) **ne doit jamais** remonter sans passer par
l'`ExceptionControllerAdvice` — sinon fuite de stack trace.

## Tests de contrôleur — avec auth

### Utilitaires partagés

| Classe | Usage |
|---|---|
| `AuthenticatedUserTestHelper.AUTH_ADMIN` | `JwtAuthenticationToken` avec rôle `ADMIN` |
| `AuthenticatedUserTestHelper.AUTH_INTERVIEWER` | idem, rôle `INTERVIEWER` (id `INTW1`) |
| `AuthenticatedUserTestHelper.AUTH_LOCAL_USER` | idem, rôle `LOCAL_USER` (id `abc`) |
| `AuthenticatedUserTestHelper.NOT_AUTHENTICATED` | `AnonymousAuthenticationToken` |
| `AuthenticatedUserTestHelper.getAuthenticatedUser(id, roles…)` | constructeur ad-hoc |
| `AuthenticatedUserServiceFake` | fake du port `AuthenticatedUserService` |
| `MockMvcTestUtils.createExceptionControllerAdvice()` | advice partagé pour `standaloneSetup` |

### Test MockMvc avec authentification (standalone)

```java
@BeforeEach
void setup() {
    campaignService = new CampaignServiceFake();
    authService = new AuthenticatedUserServiceFake(AuthenticatedUserTestHelper.AUTH_ADMIN);
    var controller = new CampaignController(campaignService, authService);
    mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
        .build();
}

@Test
@DisplayName("Should return 403 when user has no admin role")
void shouldReturn403WhenNotAdmin() throws Exception {
    authService.setCurrentUser(AuthenticatedUserTestHelper.AUTH_INTERVIEWER);

    mockMvc.perform(delete("/api/campaign/id").contentType(APPLICATION_JSON))
        .andExpect(status().isForbidden());
}
```

Note : `standaloneSetup` ne déclenche **pas** la chaîne Spring Security. Pour
tester les règles `requestMatchers(...).hasAnyRole(...)`, utiliser un test
d'intégration (section suivante).

### Test d'intégration avec auth réelle

```java
@ActiveProfiles({"auth", "test"})
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Transactional
class CampaignIT {

    @Autowired MockMvc mockMvc;

    @Test
    void shouldReturn200ForAdmin() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                .with(authentication(AuthenticatedUserTestHelper.AUTH_ADMIN))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturn403ForInterviewerOnAdminEndpoint() throws Exception {
        mockMvc.perform(delete("/api/campaign/SIMPSONS2020X00")
                .with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER))
                .contentType(APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/campaigns").contentType(APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
```

### Scénarios de sécurité à couvrir pour tout endpoint

| Scénario | Code attendu |
|---|---|
| Utilisateur non authentifié | 401 |
| Authentifié, rôle insuffisant | 403 |
| Authentifié, rôle valide, ressource inexistante | 404 |
| Authentifié, rôle valide, happy path | 200 / 201 |
| `OPTIONS` (CORS preflight) | 200 / 204 |

## Legacy vs cible

| Aspect | Legacy | Cible |
|---|---|---|
| Vérification de rôle | `SecurityContextHolder` en dur | Port `AuthenticatedUserService` |
| Test d'auth | `@WithMockUser` | `AuthenticatedUserTestHelper` + `.with(authentication(...))` |
| Fake auth | `dummy/AuthenticationUserFakeService` | `fake/AuthenticatedUserServiceFake` |
| 401/403 | `ResponseEntity.status(403)` direct | `AccessDeniedException` → `ExceptionControllerAdvice` |

## Check-list sécurité pour une nouvelle feature

- [ ] Endpoint ajouté à `OidcSecurityConfiguration.authorizeRequests()` avec les rôles requis
- [ ] Le contrôleur passe par `AuthenticatedUserService` (jamais `SecurityContextHolder`)
- [ ] Test d'intégration 401 (non authentifié)
- [ ] Test d'intégration 403 (rôle insuffisant)
- [ ] Test d'intégration 200 (rôle autorisé)
- [ ] Pas de nouveau `permitAll()` ajouté sans justification
- [ ] Session reste `STATELESS` (pas de réintroduction de session HTTP)
- [ ] CSRF reste désactivé (sauf réintroduction de session → réactivation CSRF obligatoire)
- [ ] Aucun secret / token en clair dans les logs
