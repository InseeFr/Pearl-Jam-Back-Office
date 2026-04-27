# 📋 Checklist d'Implémentation - SurveyUnitToReview Endpoint

## 🎯 Besoin Synthétique

**Objectif** : Remplacer l'ancien endpoint `/api/campaign/{campaignId}/survey-units?state=TBR` par un nouveau endpoint paginé `/api/reporting/survey-units/to-review` offrant de meilleures performances

**Fonctionnalités requises** :
- Pagination côté serveur (page, size, sort)
- Réponse structurée avec métadonnées de pagination
- Intégration avec l'authentification existante
- Respect de l'architecture hexagonale

## 🚀 Plan d'Implémentation Détaillé

### Phase 1 : Préparation et Configuration ✅

- [x] **1.1** Créer le fichier `checklist.md` (en cours)
- [x] **1.2** Vérifier la branche de travail et l'état du dépôt

### Phase 2 : Couche Domain - Services et Ports ✅

- [x] **2.1** Créer l'interface `SurveyUnitToReviewPort`
  - Location: `pearljam-domain/src/main/java/fr/insee/pearljam/domain/reporting/port/in/SurveyUnitToReviewPort.java`
  - Méthode: `Page<SurveyUnitToReview> getSurveyUnitsToReview(String userId, String search, Pageable pageable, SurveyUnitToReviewPresenter presenter)`
  - Documentation JavaDoc complète

- [x] **2.2** Créer le modèle `SurveyUnitToReview`
  - Location: `pearljam-domain/src/main/java/fr/insee/pearljam/domain/reporting/readmodel/SurveyUnitToReview.java`
  - Champs: id, campaignId, campaignLabel, contactOutcome, interviewerId, interviewerName, viewed, lastComment
  - Record immutable

- [x] **2.3** Créer l'interface `SurveyUnitToReviewPresenter`
  - Location: `pearljam-domain/src/main/java/fr/insee/pearljam/domain/reporting/port/in/SurveyUnitToReviewPresenter.java`
  - Méthode: `SurveyUnitToReviewResponse present(Page<SurveyUnitToReview> surveyUnits)`

- [x] **2.4** Implémenter `SurveyUnitToReviewService`
  - Location: `pearljam-domain/src/main/java/fr/insee/pearljam/domain/reporting/service/SurveyUnitToReviewService.java`
  - Implémente `SurveyUnitToReviewPort`
  - Utilise `SurveyUnitService.getSurveyUnitByCampaign(userId, campaignId, StateType.TBR)`
  - Gère la pagination avec Spring Data
  - Appelle le presenter pour formater la réponse
  - Logging approprié

### Phase 3 : Couche API - Controller et DTOs ✅

- [x] **3.1** Ajouter la constante API
  - Location: `pearljam-shared-dto/src/main/java/fr/insee/pearljam/contracts/constants/Constants.java`
  - Constante: `API_REPORTING_SURVEY_UNITS_TO_REVIEW = "/api/reporting/survey-units/to-review"`

- [x] **3.2** Créer le DTO de réponse paginée
  - Location: `pearljam-api/src/main/java/fr/insee/pearljam/api/reporting/response/SurveyUnitToReviewResponse.java`
  - Champs: content (List), page, size, totalElements, totalPages
  - Record avec annotation Swagger

- [x] **3.3** Créer le DTO d'élément
  - Location: `pearljam-api/src/main/java/fr/insee/pearljam/api/reporting/response/SurveyUnitToReviewDto.java`
  - Champs: id, campaignLabel, contactOutcome, interviewerNameLabel, viewed, readOnlyUrl, lastComment
  - Record avec annotation Swagger

- [x] **3.4** Implémenter le presenter
  - Location: `pearljam-api/src/main/java/fr/insee/pearljam/api/reporting/presenter/SurveyUnitToReviewPresenter.java`
  - Implémente `SurveyUnitToReviewPresenter<SurveyUnitToReviewResponse>`
  - Méthode `present()` qui transforme `Page<SurveyUnitToReview>` en `SurveyUnitToReviewResponse`
  - Annotation `@Component`

- [x] **3.5** Implémenter le controller
  - Location: `pearljam-api/src/main/java/fr/insee/pearljam/api/reporting/controller/SurveyUnitToReviewController.java`
  - Annotation: `@RestController`, `@Tag(name = "13. Reporting")`
  - Endpoint: `@GetMapping(Constants.API_REPORTING_SURVEY_UNITS_TO_REVIEW)`
  - Paramètres: page, size, sort, search (tous optionnels)
  - Injection: `SurveyUnitToReviewPort`, `SurveyUnitToReviewPresenter`, `AuthenticatedUserService`
  - Appel au service avec le presenter
  - Logging complet
  - Documentation Swagger avec `@Operation`

### Phase 4 : Intégration et Configuration ✅

- [ ] **4.1** Configurer Spring Data Pagination
  - Vérifier la configuration de `Pageable` dans `application.properties`
  - Définir les valeurs par défaut (page=0, size=20)

- [ ] **4.2** Intégrer avec le système d'authentification
  - Utiliser `AuthenticatedUserService.getCurrentUserId()`
  - Vérifier les rôles appropriés (reviewer, local user, national user)


### Phase 5 : Documentation et Finalisation ✅

- [ ] **5.1** Mettre à jour la documentation Swagger
  - Ajouter des descriptions détaillées
  - Documenter les paramètres et réponses

- [ ] **5.2** Documenter le code
  - Ajouter JavaDoc complet sur toutes les classes/méthodes
  - Documenter les décisions d'architecture

- [ ] **5.3** Préparer la migration
  - Ajouter `@Deprecated` sur l'ancien endpoint
  - Documenter la procédure de migration


## ✅ Critères d'Acceptation

1. L'endpoint `/api/reporting/survey-units/to-review` est fonctionnel
2. La pagination fonctionne correctement
4. Les performances sont améliorées par rapport à l'ancien endpoint
5. La documentation est complète et à jour
6. Le code respecte les standards de craftsmanship définis

## 📋 Points d'Attention et Décisions d'Architecture

### 1. Champs Campagne Simplifiés
- **Décision** : Le modèle `SurveyUnitToReview` a été simplifié en supprimant les champs `campaignId` et `campaignLabel`
- **Raison** : Ces informations ne sont pas disponibles dans le `SurveyUnitCampaignDto` existant
- **Solution future** : Ces champs pourront être ajoutés lors de l'implémentation de l'infrastructure avec une requête SQL dédiée incluant les informations de campagne

### 2. Pagination Manuelle
- **Implémentation actuelle** : La pagination est gérée manuellement dans le service car `SurveyUnitService.getSurveyUnitByCampaign()` retourne un `Set` non paginé
- **Optimisation future** : Créer une méthode repository native avec pagination SQL pour de meilleures performances

### 3. Intégration avec Service Existant
- **Approche** : Réutilisation de `SurveyUnitService.getSurveyUnitByCampaign(userId, null, StateType.TBR)` pour maintenir la cohérence
- **Avantage** : Réduit la duplication de code et garantit la cohérence des règles métier
- **Inconvénient** : Performance potentiellement sous-optimale pour les grands jeux de données

### 4. Gestion des Erreurs
- **Implémenté** : Logging approprié et gestion des cas où aucun résultat n'est trouvé
- **À améliorer** : Ajouter des exceptions métier spécifiques pour les cas d'erreur

### 5. Architecture Hexagonale
- **Respectée** : Séparation claire entre ports (interfaces) et adaptateurs (implémentations)
- **À compléter** : L'implémentation du repository port dans l'infrastructure

