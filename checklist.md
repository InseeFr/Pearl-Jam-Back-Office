# Checklist for Adding lastUpdated Attribute to SurveyUnit

## Database Migration via Liquibase

- [x] Create a new Liquibase changelog file (e.g., `630_add_last_updated_to_survey_unit.xml`) in `src/main/resources/db/changelog/`
- [x] Add the `lastUpdated` column to the `survey_unit` table with type `BIGINT`
- [x] Set default value to `0` for the `lastUpdated` column
- [x] Include the new changelog in `src/main/resources/db/master.xml`

## Entity Modification

- [x] Add `lastUpdated` field of type `long` to the `SurveyUnit` entity class (`src/main/java/fr/insee/pearljam/api/domain/SurveyUnit.java`)
- [x] Add `@Column` annotation with appropriate configuration
- [x] Set default value to `0L` in the field declaration
- [x] Update constructors if necessary to include the new field

## DTO Updates

- [x] Add `lastUpdated` field to `SurveyUnitDto` class (`src/main/java/fr/insee/pearljam/api/dto/surveyunit/SurveyUnitDto.java`)
- [x] Add `lastUpdated` field to `SurveyUnitDetailDto` class (`src/main/java/fr/insee/pearljam/api/dto/surveyunit/SurveyUnitDetailDto.java`)
- [x] Update the corresponding builders/mappers to handle the new field

## Service Layer Updates

- [x] Update `SurveyUnitService` to set `lastUpdated` timestamp when updating survey units
- [x] Inject `DateService` into `SurveyUnitService` if not already present
- [x] Use `DateService.getCurrentTimestamp()` to set the `lastUpdated` field during update operations

## Controller Modifications

- [x] Update `SurveyUnitController.updateSurveyUnit()` method to ensure `lastUpdated` is set and returned
- [x] Verify that GET endpoints include the `lastUpdated` field in responses

## Repository Updates

- [x] Update `SurveyUnitRepository` if any custom queries need to include the `lastUpdated` field

## Testing

- [x] Create/update unit tests for the new functionality
- [x] Test the database migration
- [x] Test the GET endpoint response includes `lastUpdated`
- [x] Test the PUT endpoint updates and returns `lastUpdated`
