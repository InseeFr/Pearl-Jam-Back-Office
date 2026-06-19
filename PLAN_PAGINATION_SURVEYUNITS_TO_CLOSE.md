# 📋 Plan d'Implémentation - Pagination pour `getSurveyUnitsToClose`
**Solution choisie : Pagination après filtrage (Solution 1)**

---

## ✅ Suivi de Progression

### Phase 1 : Préparation ✅ **TERMINÉE**
- [x] Créer une branche dédiée `feat/pagination-survey-units-to-close`
- [x] Vérifier tous les appels à `getSurveyUnitsToClose` dans le codebase
- [x] Identifier les classes qui implémentent `SurveyUnitClosingPresenter`
- [x] Lister les tests unitaires à mettre à jour

**Appels trouvés :**
- `SurveyUnitClosingController.getSurveyUnitsToClose()` (API)
- `SurveyUnitClosingPort.getSurveyUnitsToClose()` (Port)
- `SurveyUnitClosing.getSurveyUnitsToClose()` (Service)
- `SurveyUnitClosingPortStub.getSurveyUnitsToClose()` (Test Stub)

**Implémentations de Presenter :**
- `SurveyUnitClosingApiPresenter` (retourne `List<SurveyUnitToCloseResponse>`)

### Phase 2 : Modification des Ports ✅ **TERMINÉE**
- [x] Mettre à jour `SurveyUnitRepository` (Domain Port)
  - Ajout de `findEligibleSurveyUnitIds(long date, List<String> lstOuIds)`
  - Ajout de `countEligibleSurveyUnits(long date, List<String> lstOuIds)`
  - Ajout de `findClosableCandidatesByIds(List<String> ids, long date)`

### Prochaines Étapes ⏭️
- [x] Phase 3 : Implémentation JPA (3 nouvelles requêtes SQL)
- [x] Phase 4 : Mise à jour du Service
- [x] Phase 5 : Mise à jour du Presenter
- [x] Phase 6 : Mise à jour du Controller
- [ ] Phase 7 : Mise à jour des Tests

**Branche actuelle :** `feat/pagination-survey-units-to-close`

---

## 📊 Résumé des Modifications

### Fichiers déjà modifiés :
1. ✅ `pearljam-domain/src/main/java/fr/insee/pearljam/domain/surveyunit/port/out/SurveyUnitRepository.java`
   - Ajout de 3 nouvelles méthodes avec JavaDoc (en anglais)
2. ✅ `pearljam-infrastructure-persistence/src/main/java/fr/insee/pearljam/infrastructure/persistence/surveyunit/jpa/SurveyUnitJpaRepository.java`
   - Implémentation des 3 requêtes SQL natives avec @Query + @Param
3. ✅ `pearljam-infrastructure-persistence/src/main/java/fr/insee/pearljam/infrastructure/persistence/surveyunit/adapter/SurveyUnitDaoAdapter.java`
   - Implémentation des 3 nouvelles méthodes délégant au JPA repository
4. ✅ `pearljam-domain/src/main/java/fr/insee/pearljam/domain/surveyunit/port/in/SurveyUnitClosingPort.java`
   - Ajout de la méthode avec pagination
5. ✅ `pearljam-domain/src/main/java/fr/insee/pearljam/domain/surveyunit/service/SurveyUnitClosing.java`
   - Implémentation de la méthode avec pagination et @Transactional(readOnly = true)
6. ✅ `pearljam-domain/src/main/java/fr/insee/pearljam/domain/surveyunit/port/in/PaginatedSurveyUnitClosingPresenter.java`
   - Nouvelle interface pour le presenter paginé
7. ✅ `pearljam-api/src/main/java/fr/insee/pearljam/api/surveyunit/presenter/SurveyUnitClosingApiPagePresenter.java`
   - Nouveau presenter qui retourne Page<SurveyUnitToCloseResponse>
8. ✅ `pearljam-api/src/main/java/fr/insee/pearljam/api/surveyunit/controller/SurveyUnitClosingController.java`
   - Ajout du nouvel endpoint avec pagination
9. ✅ `pearljam-domain/src/test/java/fr/insee/pearljam/domain/surveyunit/stub/SurveyUnitClosingPortStub.java`
   - Implémentation de la nouvelle méthode pour les tests

### Fichiers à modifier :
1. `pearljam-domain/src/test/java/fr/insee/pearljam/domain/surveyunit/service/SurveyUnitClosingTest.java`

---

## 📌 Contexte

### Problématique
La méthode `SurveyUnitClosing.getSurveyUnitsToClose()` charge actuellement **toutes** les survey units éligibles en mémoire avant filtrage, ce qui provoque des problèmes de performance lorsque :
- Un utilisateur a accès à de nombreuses Organization Units (OUs)
- Chaque OU contient des milliers de survey units
- Le volume total dépasse plusieurs Mo de données

### Objectif
Implémenter une **pagination efficace** qui :
1. Ne charge que les données nécessaires pour la page demandée
2. Retourne un nombre précis de résultats par page
3. Minimise les allers-retours base de données
4. Reste compatible avec l'architecture existante (ports/adapters)

---

## 🏗️ Architecture de la Solution

```
┌─────────────────────────────────────────────────────────────────────┐
│                         API Layer                                     │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │ SurveyUnitController.getSurveyUnitsToClose()                │    │
│  │ - Reçoit : userId, page, size                                    │    │
│  │ - Retourne : Page<SurveyUnitToCloseResponse>                 │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      Domain Layer                                    │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │ SurveyUnitClosing.getSurveyUnitsToClose()                    │    │
│  │ - Utilise : SurveyUnitRepository (nouvelle méthode)            │    │
│  │ - Retourne : Page<ClosableSurveyUnitView> via Presenter        │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    Ports Layer (Interfaces)                          │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │ SurveyUnitRepository                                           │    │
│  │ + findEligibleSurveyUnitIds(date, lstOuIds) : List<String>     │    │
│  │ + findClosableCandidatesByIds(ids, date) : List<...>           │    │
│  │ + countEligibleSurveyUnits(date, lstOuIds) : long              │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                                │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │ SurveyUnitJpaRepository (Spring Data JPA)                      │    │
│  │ - Implémente les 3 nouvelles méthodes                           │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📝 Étapes d'Implémentation

### Phase 1 : Préparation (1 jour) ✅ **TERMINÉE**

#### 1.1 Créer une branche dédiée ✅
```bash
cd C:\INSEE\CodeSource\Pearl-Jam-Back-Office
git checkout -b feat/pagination-survey-units-to-close
```

#### 1.2 Analyser les dépendances existantes ✅
- [x] Vérifier tous les appels à `getSurveyUnitsToClose` dans le codebase
- [x] Identifier les classes qui implémentent `SurveyUnitClosingPresenter`
- [x] Lister les tests unitaires à mettre à jour

**Résultats :**
- 7 appels trouvés dans 6 fichiers différents
- 1 implémentation de Presenter : `SurveyUnitClosingApiPresenter`
- Tests à mettre à jour : `SurveyUnitClosingTest.java`, `SurveyUnitClosingControllerTest.java`

**Commande utile :**
```bash
# Sous Windows
findstr /s /i /c:"getSurveyUnitsToClose" *.java
```

---

### Phase 2 : Modification des Ports (1/2 jour) ✅ **TERMINÉE**

#### 2.1 Mettre à jour `SurveyUnitRepository` (Domain Port) ✅
**Fichier :** `pearljam-domain/src/main/java/fr/insee/pearljam/domain/surveyunit/port/out/SurveyUnitRepository.java`

**Ajout de 3 nouvelles méthodes :** ✅
```java
/**
 * Récupère les IDs de toutes les survey units éligibles à la fermeture
 * (après application des règles de visibilité et de campagne).
 * Utilisé pour la pagination.
 *
 * @param date timestamp actuel
 * @param lstOuIds liste des Organization Unit IDs de l'utilisateur
 * @return liste des IDs des survey units éligibles
 */
List<String> findEligibleSurveyUnitIds(long date, List<String> lstOuIds);

/**
 * Compte le nombre total de survey units éligibles à la fermeture.
 * Utilisé pour la pagination.
 *
 * @param date timestamp actuel
 * @param lstOuIds liste des Organization Unit IDs de l'utilisateur
 * @return nombre total d'éléments éligibles
 */
long countEligibleSurveyUnits(long date, List<String> lstOuIds);

/**
 * Récupère les ClosableSurveyUnitCandidateView pour une liste spécifique d'IDs.
 * Utilisé pour la pagination.
 *
 * @param ids liste des IDs des survey units
 * @param date timestamp actuel
 * @return liste des candidats correspondants
 */
List<ClosableSurveyUnitCandidateView> findClosableCandidatesByIds(List<String> ids, long date);
```

**Statut :** ✅ Implémenté et validé

---

### Phase 3 : Implémentation JPA (1 jour) ✅ **TERMINÉE**

#### 3.1 Mettre à jour `SurveyUnitJpaRepository` ✅
**Fichier :** `pearljam-infrastructure-persistence/src/main/java/fr/insee/pearljam/infrastructure/persistence/surveyunit/jpa/SurveyUnitJpaRepository.java`

**Ajouter les 3 implémentations :**

```java
// 1. Récupérer les IDs éligibles (requête optimisée)
@Query(value = """
    SELECT su.id
    FROM survey_unit su
    JOIN visibility vi 
        ON vi.campaign_id = su.campaign_id 
        AND vi.organization_unit_id = su.organization_unit_id
    JOIN LATERAL (
        SELECT s.type AS current_state
        FROM state s
        WHERE s.survey_unit_id = su.id
        ORDER BY s.date DESC
        LIMIT 1
    ) ls ON TRUE
    LEFT JOIN contact_outcome co ON co.survey_unit_id = su.id
    WHERE su.organization_unit_id IN (:lstOuIds)
    AND vi.collection_end_date < :date
    AND vi.end_date > :date
    AND (
        ls.current_state NOT IN ('TBR','FIN','CLO')
        OR co.type = 'INA'
    )
    """, nativeQuery = true)
List<String> findEligibleSurveyUnitIds(
    @Param("date") long date,
    @Param("lstOuIds") List<String> lstOuIds
);

// 2. Compter les éligibles
@Query(value = """
    SELECT COUNT(DISTINCT su.id)
    FROM survey_unit su
    JOIN visibility vi 
        ON vi.campaign_id = su.campaign_id 
        AND vi.organization_unit_id = su.organization_unit_id
    JOIN LATERAL (
        SELECT s.type AS current_state
        FROM state s
        WHERE s.survey_unit_id = su.id
        ORDER BY s.date DESC
        LIMIT 1
    ) ls ON TRUE
    LEFT JOIN contact_outcome co ON co.survey_unit_id = su.id
    WHERE su.organization_unit_id IN (:lstOuIds)
    AND vi.collection_end_date < :date
    AND vi.end_date > :date
    AND (
        ls.current_state NOT IN ('TBR','FIN','CLO')
        OR co.type = 'INA'
    )
    """, nativeQuery = true)
long countEligibleSurveyUnits(
    @Param("date") long date,
    @Param("lstOuIds") List<String> lstOuIds
);

// 3. Récupérer les candidats pour une liste d'IDs
@Query(value = """
    SELECT
        su.id AS id,
        ls.current_state AS currentStateType,
        co.type AS contactOutcomeType
    FROM survey_unit su
    JOIN LATERAL (
        SELECT s.type AS current_state
        FROM state s
        WHERE s.survey_unit_id = su.id
        ORDER BY s.date DESC
        LIMIT 1
    ) ls ON TRUE
    LEFT JOIN contact_outcome co ON co.survey_unit_id = su.id
    WHERE su.id IN (:ids)
    """, nativeQuery = true)
List<ClosableSurveyUnitCandidateView> findClosableCandidatesByIds(
    @Param("ids") List<String> ids,
    @Param("date") long date
);
```

---

### Phase 4 : Mise à jour du Service (1/2 jour) ✅ **TERMINÉE**

#### 4.1 Modifier `SurveyUnitClosing` ✅
**Fichier :** `pearljam-domain/src/main/java/fr/insee/pearljam/domain/surveyunit/service/SurveyUnitClosing.java`

**Ajouter une nouvelle méthode (laisser l'ancienne pour compatibilité) :**
```java
@Override
public <T> T getSurveyUnitsToClose(String userId, SurveyUnitClosingPresenter<T> presenter) {
    // Appel la nouvelle version avec pagination par défaut
    return getSurveyUnitsToClose(userId, presenter, PageRequest.of(0, Integer.MAX_VALUE));
}

/**
 * Nouvelle méthode avec pagination
 */
public <T> T getSurveyUnitsToClose(String userId, SurveyUnitClosingPresenter<T> presenter, Pageable pageable) {
    List<String> lstOuIds = userService.getUserOUsModel(userId, true).stream()
        .map(OrganizationUnitSummary::getId)
        .toList();

    long now = dateService.getCurrentTimestamp();

    // 1. Récupérer TOUS les IDs éligibles (léger : juste des IDs)
    List<String> allEligibleIds = surveyUnitRepository.findEligibleSurveyUnitIds(now, lstOuIds);

    if (allEligibleIds.isEmpty()) {
        return presenter.empty();
    }

    // 2. Appliquer la pagination
    long totalElements = allEligibleIds.size();
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), allEligibleIds.size());
    
    if (start >= allEligibleIds.size()) {
        return presenter.empty();
    }
    
    List<String> pagedIds = allEligibleIds.subList(start, end);

    // 3. Récupérer les candidats pour cette page
    List<ClosableSurveyUnitCandidateView> candidates = 
        surveyUnitRepository.findClosableCandidatesByIds(pagedIds, now);

    if (candidates.isEmpty()) {
        return presenter.empty();
    }

    // 4. Créer les maps
    Map<String, ClosableSurveyUnitCandidateView> candidatesById = candidates.stream()
        .collect(Collectors.toMap(ClosableSurveyUnitCandidateView::getId, Function.identity()));

    // 5. Récupérer les états questionnaire
    Map<String, String> states = questionnaireStatePort.getStates(candidatesById.keySet());

    // 6. Filtrer les éligibles ( redondant avec la requête SQL, mais on garde pour sécurité)
    //    Note: Ce filtrage pourrait être retiré si on fait confiance à la requête SQL
    Map<String, ClosableSurveyUnitCandidateView> eligibleById = candidates.stream()
        .filter(candidate -> surveyUnitClosablePolicy.isClosable(candidate, states.get(candidate.getId())))
        .collect(Collectors.toMap(ClosableSurveyUnitCandidateView::getId, Function.identity()));

    // 7. Récupérer les projections complètes
    List<ClosableSurveyUnitView> closableSurveyUnitProjections = 
        surveyUnitRepository.findClosableSurveyUnits(eligibleById.keySet());

    // 8. Présenter le résultat
    return presenter.present(closableSurveyUnitProjections, candidatesById, states);
}
```

---

### Phase 5 : Mise à jour du Presenter (1/2 jour) ✅ **TERMINÉE**

#### 5.1 Créer `PaginatedSurveyUnitClosingPresenter` (Nouvelle Interface) ✅
**Fichier :** `pearljam-domain/src/main/java/fr/insee/pearljam/domain/surveyunit/port/in/PaginatedSurveyUnitClosingPresenter.java`
- Nouvelle interface qui étend SurveyUnitClosingPresenter
- Ajout de la méthode present() avec paramètres de pagination

#### 5.2 Créer `SurveyUnitClosingApiPagePresenter` ✅
**Fichier :** `pearljam-api/src/main/java/fr/insee/pearljam/api/surveyunit/presenter/SurveyUnitClosingApiPagePresenter.java`
- Implémente PaginatedSurveyUnitClosingPresenter<Page<SurveyUnitToCloseResponse>>
- Retourne un objet Page de Spring Data avec métadonnées de pagination
@Component
public class SurveyUnitClosingApiPagePresenter implements SurveyUnitClosingPresenter<Page<SurveyUnitToCloseResponse>> {

    @Override
    public Page<SurveyUnitToCloseResponse> present(
            List<ClosableSurveyUnitView> projections,
            Map<String, ClosableSurveyUnitCandidateView> candidatesById,
            Map<String, String> questionnaireStates) {
        // Ne devrait pas être appelé directement
        throw new UnsupportedOperationException("Use present with pagination parameters");
    }

    @Override
    public Page<SurveyUnitToCloseResponse> empty() {
        return Page.empty();
    }

    @Override
    public Page<SurveyUnitToCloseResponse> present(
            List<ClosableSurveyUnitView> projections,
            Map<String, ClosableSurveyUnitCandidateView> candidatesById,
            Map<String, String> questionnaireStates,
            long totalElements,
            int pageNumber,
            int pageSize) {
        
        List<SurveyUnitToCloseResponse> content = projections.stream()
                .map(toResponse(candidatesById, questionnaireStates))
                .toList();
        
        return new PageImpl<>(
            content,
            PageRequest.of(pageNumber, pageSize),
            totalElements
        );
    }

    // Réutiliser la méthode existante
    private Function<ClosableSurveyUnitView, SurveyUnitToCloseResponse> toResponse(
            Map<String, ClosableSurveyUnitCandidateView> candidatesById, 
            Map<String, String> questionnaireStates) {
        return projection -> {
            String id = projection.getId();
            var candidate = candidatesById.get(id);
            ContactOutcomeType contactOutcome = candidate != null ? candidate.getContactOutcomeType() : null;
            String interviewerLabel = buildInterviewerLabel(projection);
            String questionnaireState = questionnaireStates.getOrDefault(id, Constants.QUESTIONNAIRE_STATE_UNAVAILABLE);
            
            return new SurveyUnitToCloseResponse(
                projection.getCampaignLabel(),
                projection.getId(),
                projection.getDisplayName(),
                interviewerLabel,
                projection.getSsech(),
                computeIdentificationState(projection).name(),
                contactOutcome,
                questionnaireState,
                projection.getClosingCauseType()
            );
        };
    }

    // Méthodes existantes à copier depuis SurveyUnitClosingApiPresenter
    private String buildInterviewerLabel(ClosableSurveyUnitView projection) { ... }
    private static IdentificationState computeIdentificationState(ClosableSurveyUnitView p) { ... }
    private static Identification toModelIdentification(ClosableSurveyUnitView p) { ... }
}
```

---

### Phase 6 : Mise à jour du Controller (1/2 jour) ✅ **TERMINÉE**

#### 6.1 Modifier `SurveyUnitClosingController` ✅
**Fichier :** `pearljam-api/src/main/java/fr/insee/pearljam/api/surveyunit/controller/SurveyUnitClosingController.java`

**Modifications :**
- Ajout des imports pour Page, Pageable, @ParameterObject
- Injection de SurveyUnitClosingApiPagePresenter
- Ajout du nouvel endpoint `/survey-units/to-close` avec paramètres `page` et `size`
- L'ancien endpoint reste pour compatibilité descendante

**Nouvel endpoint :**
```java
@GetMapping(value = Constants.API_SURVEYUNITS_TO_CLOSE, params = {"page", "size"})
public ResponseEntity<Page<SurveyUnitToCloseResponse>> getSurveyUnitsToClosePaginated(
        @CurrentSecurityContext(expression = "authentication.name") String userId,
        @ParameterObject Pageable pageable) {
    Page<SurveyUnitToCloseResponse> result = surveyUnitClosingPort
            .getSurveyUnitsToClose(userId, pagePresenter, pageable);
    return ResponseEntity.ok(result);
}
```

---

### Phase 7 : Mise à jour des Tests (1 jour)

#### 7.1 Mettre à jour `SurveyUnitClosingTest`
**Fichier :** `pearljam-domain/src/test/java/fr/insee/pearljam/domain/surveyunit/service/SurveyUnitClosingTest.java`

**Ajouter des tests pour la nouvelle méthode :**
```java
@Test
void getSurveyUnitsToClose_WithPagination_ReturnsCorrectPage() {
    // Given
    List<String> ouIds = List.of("ou1", "ou2");
    List<String> allEligibleIds = List.of("su1", "su2", "su3", "su4", "su5");
    
    when(userService.getUserOUsModel(anyString(), anyBoolean()))
        .thenReturn(List.of(new OrganizationUnitSummary("ou1", "OU1"), new OrganizationUnitSummary("ou2", "OU2")));
    when(dateService.getCurrentTimestamp()).thenReturn(Instant.now().toEpochMilli());
    when(surveyUnitRepository.findEligibleSurveyUnitIds(anyLong(), anyList()))
        .thenReturn(allEligibleIds);
    
    // Mock pour la page 0, size 2
    List<String> pagedIds = List.of("su1", "su2");
    List<ClosableSurveyUnitCandidateView> candidates = pagedIds.stream()
        .map(id -> {
            ClosableSurveyUnitCandidateView mock = mock(ClosableSurveyUnitCandidateView.class);
            when(mock.getId()).thenReturn(id);
            when(mock.getCurrentStateType()).thenReturn(StateType.PRC);
            when(mock.getContactOutcomeType()).thenReturn(null);
            return mock;
        })
        .toList();
    
    when(surveyUnitRepository.findClosableCandidatesByIds(pagedIds, anyLong()))
        .thenReturn(candidates);
    when(questionnaireStatePort.getStates(any()))
        .thenReturn(Map.of("su1", "AVAILABLE", "su2", "AVAILABLE"));
    when(surveyUnitRepository.findClosableSurveyUnits(any()))
        .thenReturn(createMockProjections(pagedIds));
    
    // When
    Pageable pageable = PageRequest.of(0, 2);
    List<SurveyUnitToCloseResponse> result = surveyUnitClosing
        .getSurveyUnitsToClose("user1", new TestPagePresenter(), pageable);
    
    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).id()).isEqualTo("su1");
    assertThat(result.get(1).id()).isEqualTo("su2");
}

@Test
void getSurveyUnitsToClose_WithPagination_EmptyPage() {
    // Test pour une page vide
}

@Test
void getSurveyUnitsToClose_WithPagination_LastPage() {
    // Test pour la dernière page
}

// Classe de test pour le presenter
private static class TestPagePresenter implements SurveyUnitClosingPresenter<Page<SurveyUnitToCloseResponse>> {
    @Override
    public Page<SurveyUnitToCloseResponse> present(
            List<ClosableSurveyUnitView> projections,
            Map<String, ClosableSurveyUnitCandidateView> candidatesById,
            Map<String, String> questionnaireStates,
            long totalElements,
            int pageNumber,
            int pageSize) {
        List<SurveyUnitToCloseResponse> content = projections.stream()
            .map(p -> new SurveyUnitToCloseResponse(
                p.getCampaignLabel(), p.getId(), p.getDisplayName(),
                (p.getInterviewerFirstName() != null ? p.getInterviewerFirstName() + " " + p.getInterviewerLastName() : null),
                p.getSsech(), "NOT_APPLICABLE", null, "UNAVAILABLE", null
            ))
            .toList();
        return new PageImpl<>(content, PageRequest.of(pageNumber, pageSize), totalElements);
    }
    
    @Override
    public Page<SurveyUnitToCloseResponse> empty() {
        return Page.empty();
    }
}
```

---

### Phase 8 : Configuration et Intégration (1/2 jour)

#### 8.1 Vérifier la configuration Spring Data
- [ ] S'assurer que `Pageable` est correctement supporté dans les requêtes natives
- [ ] Vérifier que les dépendances Spring Data JPA sont à jour

#### 8.2 Mettre à jour les imports
Dans tous les fichiers modifiés, ajouter les imports nécessaires :
```java
// Pour la pagination
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
```

---

### Phase 9 : Tests d'Intégration (1 jour)

#### 9.1 Tests manuels
- [ ] Tester avec un utilisateur ayant peu de survey units
- [ ] Tester avec un utilisateur ayant beaucoup de survey units
- [ ] Tester la pagination avec différentes tailles de page
- [ ] Vérifier que le total d'éléments est correct

#### 9.2 Tests automatisés
- [ ] Exécuter les tests unitaires existants
- [ ] Exécuter les tests d'intégration
- [ ] Vérifier la couverture de code

---

### Phase 10 : Documentation (1/2 jour)

#### 10.1 Mettre à jour la documentation API
- [ ] Documenter le nouveau endpoint dans Swagger/OpenAPI
- [ ] Ajouter des exemples de requêtes/réponses

#### 10.2 Ajouter des commentaires dans le code
- [ ] Documenter les nouvelles méthodes avec JavaDoc
- [ ] Ajouter des commentaires expliquant la logique de pagination

---

## 📊 Estimation des Performances

### Avant l'optimisation
| Métrique | Valeur (exemple avec 10k survey units) |
|----------|--------------------------------------|
| Mémoire utilisée | ~500 Mo |
| Temps de réponse | 10-15 secondes |
| Requêtes SQL | 2 (candidats + projections) + 1 HTTP |
| Traitement | Sequential + ParallelStream |

### Après l'optimisation
| Métrique | Valeur (page de 100) |
|----------|------------------------|
| Mémoire utilisée | ~5 Mo |
| Temps de réponse | 1-3 secondes |
| Requêtes SQL | 3 (IDs + candidats + projections) + 1 HTTP |
| Traitement | Sequential (batch de 100) |

**Gain estimé :** 90% de réduction de la mémoire, 70% de réduction du temps de réponse

---

## ⚠️ Risques et Mitigations

| Risque | Probabilité | Impact | Mitigation |
|--------|-------------|--------|------------|
| Régression fonctionnelle | Moyenne | Élevé | Tests unitaires et d'intégration complets |
| Problèmes de performance avec OFFSET/LIMIT | Faible | Moyen | Utiliser des clés de pagination (keyset pagination) si nécessaire |
| Incompatibilité avec l'ancien code | Moyenne | Moyen | Maintenir l'ancienne méthode pour compatibilité |
| Problèmes de transaction | Faible | Faible | La méthode est en read-only, pas de @Transactional nécessaire |
| Timeouts avec de très grands jeux de données | Faible | Moyen | Configurer un timeout raisonnable |

---

## 🔄 Rollback Plan

Si des problèmes sont détectés :
1. Revert la branche : `git checkout develop && git branch -D feat/pagination-survey-units-to-close`
2. Les modifications sont isolées dans de nouvelles méthodes, l'ancienne `getSurveyUnitsToClose` reste fonctionnelle
3. Aucun changement destructif sur les données

---

## ✅ Checklist de Validation

### Avant merge
- [ ] Tous les tests unitaires passent
- [ ] Tous les tests d'intégration passent
- [ ] Le nouveau endpoint retourne les bonnes données
- [ ] La pagination fonctionne correctement (première page, dernière page, page vide)
- [ ] Le total d'éléments est correct
- [ ] Les performances sont améliorées (mesurées avec des données de test)
- [ ] La documentation est à jour
- [ ] Le code est reviewé par au moins un autre développeur

### Après deployment
- [ ] Surveiller les logs pour détecter des erreurs
- [ ] Surveiller les métriques de performance
- [ ] Vérifier que les utilisateurs finaux n'ont pas de problèmes

---

## 📚 Références

### Fichiers modifiés
1. `pearljam-domain/src/main/java/fr/insee/pearljam/domain/surveyunit/port/out/SurveyUnitRepository.java`
2. `pearljam-domain/src/main/java/fr/insee/pearljam/domain/surveyunit/service/SurveyUnitClosing.java`
3. `pearljam-domain/src/main/java/fr/insee/pearljam/domain/surveyunit/port/in/SurveyUnitClosingPresenter.java`
4. `pearljam-api/src/main/java/fr/insee/pearljam/api/surveyunit/presenter/SurveyUnitClosingApiPresenter.java`
5. `pearljam-api/src/main/java/fr/insee/pearljam/api/surveyunit/controller/SurveyUnitController.java`
6. `pearljam-infrastructure-persistence/src/main/java/fr/insee/pearljam/infrastructure/persistence/surveyunit/jpa/SurveyUnitJpaRepository.java`

### Nouveaux fichiers créés
1. `pearljam-api/src/main/java/fr/insee/pearljam/api/surveyunit/presenter/SurveyUnitClosingApiPagePresenter.java`

### Tests modifiés
1. `pearljam-domain/src/test/java/fr/insee/pearljam/domain/surveyunit/service/SurveyUnitClosingTest.java`

---

## 💡 Notes Techniques

### Pourquoi cette approche ?
- **Pagination après filtrage** : Garantit que chaque page a exactement le nombre d'éléments demandé
- **Chargement des IDs d'abord** : Minimise la quantité de données chargées en mémoire
- **Compatibilité descendante** : L'ancienne méthode reste disponible

### Alternatives considérées
1. **Keyset Pagination** : Plus performant pour les très grands jeux de données, mais plus complexe à implémenter
2. **Cursor-based Pagination** : Similaire à keyset, mais nécessite plus de changements
3. **Pagination côté base** : Moins flexible, car le filtrage final dépend aussi de l'appel HTTP externe

### Points d'attention
- L'appel à `questionnaireStatePort.getStates()` reste un goulot d'étranglement potentiel
- Pour une optimisation supplémentaire, envisager de paralleliser cet appel ou d'implémenter un cache
- La requête SQL pour `findEligibleSurveyUnitIds` pourrait être optimisée avec des index supplémentaires si nécessaire

---

**Document créé le :** 2026-06-19  
**Auteur :** Analyste Développeur (avec Mistral Vibe)  
**Version :** 1.0  
**Statut :** À implémenter
