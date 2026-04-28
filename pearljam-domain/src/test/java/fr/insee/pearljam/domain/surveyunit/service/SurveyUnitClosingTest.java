package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.service.exception.ClosingCauseAlreadyExistsException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.domain.surveyunit.stub.ClosingCauseRepositoryStub;
import fr.insee.pearljam.domain.surveyunit.stub.SurveyUnitExistencePortStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SurveyUnitClosing Service Tests")
class SurveyUnitClosingTest {

    private SurveyUnitClosing surveyUnitClosing;
    private ClosingCauseRepositoryStub closingCauseRepository;
    private SurveyUnitExistencePortStub surveyUnitPort;

    @BeforeEach
    void setUp() {
        closingCauseRepository = new ClosingCauseRepositoryStub();
        surveyUnitPort = new SurveyUnitExistencePortStub();
        surveyUnitClosing = new SurveyUnitClosing(closingCauseRepository, surveyUnitPort);
    }

    @Test
    @DisplayName("Should successfully add closing cause to single survey unit")
    void shouldAddClosingCauseToSingleSurveyUnit() {
        // Given
        String surveyUnitId = "SU001";
        ClosingCauseType type = ClosingCauseType.NPA;
        List<String> surveyUnitIds = Collections.singletonList(surveyUnitId);

        surveyUnitPort.addExistingSurveyUnit(surveyUnitId);

        // When
        assertDoesNotThrow(() ->
                surveyUnitClosing.addClosingCauseToMultipleSurveyUnits(surveyUnitIds, type)
        );

        // Then
        assertTrue(closingCauseRepository.existsClosingCauseFromSurveyUnitId(surveyUnitId));
        assertEquals(1, closingCauseRepository.getAddedClosingCausesCount());
    }

    @Test
    @DisplayName("Should successfully add closing cause to multiple survey units")
    void shouldAddClosingCauseToMultipleSurveyUnits() {
        // Given
        List<String> surveyUnitIds = Arrays.asList("SU001", "SU002", "SU003");
        ClosingCauseType type = ClosingCauseType.NPI;

        surveyUnitIds.forEach(surveyUnitPort::addExistingSurveyUnit);

        // When
        assertDoesNotThrow(() ->
                surveyUnitClosing.addClosingCauseToMultipleSurveyUnits(surveyUnitIds, type)
        );

        // Then
        surveyUnitIds.forEach(id -> {
            assertTrue(closingCauseRepository.existsClosingCauseFromSurveyUnitId(id));
            assertEquals(type, closingCauseRepository.getClosingCauseType(id));
        });
        assertEquals(3, closingCauseRepository.getAddedClosingCausesCount());
    }

    @Test
    @DisplayName("Should throw SurveyUnitNotFoundException when survey unit does not exist")
    void shouldThrowExceptionWhenSurveyUnitDoesNotExist() {
        // Given
        String surveyUnitId = "NONEXISTENT";
        List<String> surveyUnitIds = Collections.singletonList(surveyUnitId);
        ClosingCauseType type = ClosingCauseType.NPX;

        // When/Then
        assertThatThrownBy(() ->
                surveyUnitClosing.addClosingCauseToMultipleSurveyUnits(surveyUnitIds, type)
        )
                .isInstanceOf(SurveyUnitNotFoundException.class)
                .hasMessageContaining(surveyUnitId);

        assertFalse(closingCauseRepository.existsClosingCauseFromSurveyUnitId(surveyUnitId));
        assertEquals(0, closingCauseRepository.getAddedClosingCausesCount());
    }

    @Test
    @DisplayName("Should throw ClosingCauseAlreadyExistsException when closing cause already exists")
    void shouldThrowExceptionWhenClosingCauseAlreadyExists() {
        // Given
        String surveyUnitId = "SU001";
        List<String> surveyUnitIds = Collections.singletonList(surveyUnitId);
        ClosingCauseType type = ClosingCauseType.ROW;

        surveyUnitPort.addExistingSurveyUnit(surveyUnitId);
        closingCauseRepository.addInitialClosingCauseToSurveyUnit(surveyUnitId, ClosingCauseType.NPA);

        // When/Then
        assertThatThrownBy(() ->
                surveyUnitClosing.addClosingCauseToMultipleSurveyUnits(surveyUnitIds, type)
        )
                .isInstanceOf(ClosingCauseAlreadyExistsException.class)
                .hasMessageContaining(surveyUnitId);

        assertEquals(0, closingCauseRepository.getAddedClosingCausesCount());
    }

    @Test
    @DisplayName("Should throw exception when some survey units don't exist - batch validation")
    void shouldThrowExceptionForMissingSurveyUnitsInBatch() {
        // Given
        List<String> surveyUnitIds = Arrays.asList("SU001", "INVALID", "SU003");
        ClosingCauseType type = ClosingCauseType.NPA;

        surveyUnitPort.addExistingSurveyUnit("SU001");
        surveyUnitPort.addExistingSurveyUnit("SU003");

        // When/Then - With batch validation, ALL are validated BEFORE processing
        assertThatThrownBy(() ->
                surveyUnitClosing.addClosingCauseToMultipleSurveyUnits(surveyUnitIds, type)
        )
                .isInstanceOf(SurveyUnitNotFoundException.class)
                .hasMessageContaining("INVALID");

        // With batch processing, NONE should be processed if validation fails
        assertFalse(closingCauseRepository.existsClosingCauseFromSurveyUnitId("SU001"));
        assertFalse(closingCauseRepository.existsClosingCauseFromSurveyUnitId("SU003"));
        assertEquals(0, closingCauseRepository.getAddedClosingCausesCount());
    }

    @Test
    @DisplayName("Should throw exception when closing cause exists for some units - batch validation")
    void shouldThrowExceptionWhenClosingCauseExistsInBatch() {
        // Given
        List<String> surveyUnitIds = Arrays.asList("SU001", "SU002", "SU003");
        ClosingCauseType type = ClosingCauseType.NPI;

        surveyUnitPort.addExistingSurveyUnit("SU001");
        surveyUnitPort.addExistingSurveyUnit("SU002");
        surveyUnitPort.addExistingSurveyUnit("SU003");

        closingCauseRepository.addInitialClosingCauseToSurveyUnit("SU002", ClosingCauseType.NPX);

        // When/Then - With batch validation, ALL are validated BEFORE processing
        assertThatThrownBy(() ->
                surveyUnitClosing.addClosingCauseToMultipleSurveyUnits(surveyUnitIds, type)
        )
                .isInstanceOf(ClosingCauseAlreadyExistsException.class)
                .hasMessageContaining("SU002");

        // With batch processing, NONE should be processed if validation fails
        assertFalse(closingCauseRepository.existsClosingCauseFromSurveyUnitId("SU001"));
        assertEquals(ClosingCauseType.NPX, closingCauseRepository.getClosingCauseType("SU002"));
        assertFalse(closingCauseRepository.existsClosingCauseFromSurveyUnitId("SU003"));
        assertEquals(0, closingCauseRepository.getAddedClosingCausesCount());
    }

    @Test
    @DisplayName("Should handle empty list of survey units")
    void shouldHandleEmptyList() {
        // Given
        List<String> surveyUnitIds = Collections.emptyList();
        ClosingCauseType type = ClosingCauseType.NPX;

        // When
        assertDoesNotThrow(() ->
                surveyUnitClosing.addClosingCauseToMultipleSurveyUnits(surveyUnitIds, type)
        );

        // Then
        assertEquals(0, closingCauseRepository.getAddedClosingCausesCount());
    }

    @Test
    @DisplayName("Should process survey units with all closing cause types")
    void shouldProcessAllClosingCauseTypes() {
        // Test each enum value
        int expectedCount = 0;
        for (ClosingCauseType type : ClosingCauseType.values()) {
            String surveyUnitId = "SU_" + type.name();
            List<String> surveyUnitIds = Collections.singletonList(surveyUnitId);

            surveyUnitPort.addExistingSurveyUnit(surveyUnitId);

            assertDoesNotThrow(() ->
                    surveyUnitClosing.addClosingCauseToMultipleSurveyUnits(surveyUnitIds, type)
            );

            assertTrue(closingCauseRepository.existsClosingCauseFromSurveyUnitId(surveyUnitId));
            assertEquals(type, closingCauseRepository.getClosingCauseType(surveyUnitId));
            expectedCount++;
        }

        assertEquals(expectedCount, closingCauseRepository.getAddedClosingCausesCount());
    }

    @Test
    @DisplayName("Should throw exception with multiple missing survey units")
    void shouldThrowExceptionWithAllMissingSurveyUnits() {
        // Given
        List<String> surveyUnitIds = Arrays.asList("INVALID1", "INVALID2", "INVALID3");
        ClosingCauseType type = ClosingCauseType.NPA;

        // When/Then
        assertThatThrownBy(() ->
                surveyUnitClosing.addClosingCauseToMultipleSurveyUnits(surveyUnitIds, type)
        )
                .isInstanceOf(SurveyUnitNotFoundException.class)
                .hasMessageContaining("INVALID1")
                .hasMessageContaining("INVALID2")
                .hasMessageContaining("INVALID3");

        assertEquals(0, closingCauseRepository.getAddedClosingCausesCount());
    }
}