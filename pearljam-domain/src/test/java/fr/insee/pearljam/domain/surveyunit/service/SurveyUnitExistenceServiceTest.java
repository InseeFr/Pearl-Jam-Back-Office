package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.service.dummy.SurveyUnitRepositoryStub;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitExistencePort;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SurveyUnit Existence Service Tests")
class SurveyUnitExistenceServiceTest {
    private SurveyUnitExistencePort surveyUnitExistencePort;
    private SurveyUnitRepository surveyUnitRepository;

    @BeforeEach
    void setUp() {
        surveyUnitRepository = new SurveyUnitRepositoryStub();
        surveyUnitExistencePort = new SurveyUnitExistenceService(surveyUnitRepository);
    }

    @Test
    @DisplayName("Should return single ID when survey unit exists")
    void shouldReturnSingleIdWhenSurveyUnitExists() {
        // Given
        String surveyUnitId = "TEST_ID";
        surveyUnitRepository.save(buildDefaultSurveyUnit(surveyUnitId));

        // When
        List<String> existingIds = surveyUnitExistencePort.findExistingIds(
                Collections.singletonList(surveyUnitId)
        );

        // Then
        assertEquals(1, existingIds.size());
        assertTrue(existingIds.contains(surveyUnitId));
    }

    @Test
    @DisplayName("Should return empty list when survey unit does not exist")
    void shouldReturnEmptyListWhenSurveyUnitDoesNotExist() {
        // Given
        String surveyUnitId = "NONEXISTENT_ID";

        // When
        List<String> existingIds = surveyUnitExistencePort.findExistingIds(
                Collections.singletonList(surveyUnitId)
        );

        // Then
        assertTrue(existingIds.isEmpty());
    }

    @Test
    @DisplayName("Should return all existing IDs from multiple survey units")
    void shouldReturnAllExistingIdsFromMultipleSurveyUnits() {
        // Given
        String id1 = "TEST_ID_1";
        String id2 = "TEST_ID_2";
        String id3 = "TEST_ID_3";

        surveyUnitRepository.save(buildDefaultSurveyUnit(id1));
        surveyUnitRepository.save(buildDefaultSurveyUnit(id2));
        surveyUnitRepository.save(buildDefaultSurveyUnit(id3));

        List<String> requestedIds = Arrays.asList(id1, id2, id3);

        // When
        List<String> existingIds = surveyUnitExistencePort.findExistingIds(requestedIds);

        // Then
        assertEquals(3, existingIds.size());
        assertTrue(existingIds.containsAll(requestedIds));
    }

    @Test
    @DisplayName("Should return only existing IDs when some survey units don't exist")
    void shouldReturnOnlyExistingIdsWhenSomeDoNotExist() {
        // Given
        String existingId1 = "EXISTING_1";
        String existingId2 = "EXISTING_2";
        String nonExistingId = "NONEXISTENT";

        surveyUnitRepository.save(buildDefaultSurveyUnit(existingId1));
        surveyUnitRepository.save(buildDefaultSurveyUnit(existingId2));

        List<String> requestedIds = Arrays.asList(existingId1, nonExistingId, existingId2);

        // When
        List<String> existingIds = surveyUnitExistencePort.findExistingIds(requestedIds);

        // Then
        assertEquals(2, existingIds.size());
        assertTrue(existingIds.contains(existingId1));
        assertTrue(existingIds.contains(existingId2));
        assertFalse(existingIds.contains(nonExistingId));
    }

    @Test
    @DisplayName("Should return empty list when no survey units exist")
    void shouldReturnEmptyListWhenNoSurveyUnitsExist() {
        // Given
        List<String> requestedIds = Arrays.asList("NONE_1", "NONE_2", "NONE_3");

        // When
        List<String> existingIds = surveyUnitExistencePort.findExistingIds(requestedIds);

        // Then
        assertTrue(existingIds.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when given empty input list")
    void shouldReturnEmptyListWhenGivenEmptyInputList() {
        // Given
        List<String> requestedIds = Collections.emptyList();

        // When
        List<String> existingIds = surveyUnitExistencePort.findExistingIds(requestedIds);

        // Then
        assertTrue(existingIds.isEmpty());
    }

    @Test
    @DisplayName("Should handle duplicate IDs in input list")
    void shouldHandleDuplicateIdsInInputList() {
        // Given
        String surveyUnitId = "TEST_ID";
        surveyUnitRepository.save(buildDefaultSurveyUnit(surveyUnitId));

        List<String> requestedIds = Arrays.asList(surveyUnitId, surveyUnitId, surveyUnitId);

        // When
        List<String> existingIds = surveyUnitExistencePort.findExistingIds(requestedIds);

        // Then
        assertFalse(existingIds.isEmpty());
        assertTrue(existingIds.contains(surveyUnitId));
        // Note: depending on implementation, this might deduplicate or not
    }

    private SurveyUnitDB buildDefaultSurveyUnit(String surveyUnitId) {
        return new SurveyUnitDB(
                surveyUnitId,
                true,
                true,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}