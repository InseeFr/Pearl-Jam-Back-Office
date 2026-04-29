package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.InterrogationOkNokDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.InterrogationOkNokResponseDto;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStateClient;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import fr.insee.pearljam.domain.surveyunit.service.exception.ClosingCauseAlreadyExistsException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.domain.surveyunit.stub.ClosingCauseRepositoryStub;
import fr.insee.pearljam.domain.surveyunit.stub.SurveyUnitExistencePortStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("SurveyUnitClosing Service Tests")
class SurveyUnitClosingTest {

    private SurveyUnitClosing surveyUnitClosing;
    private ClosingCauseRepositoryStub closingCauseRepository;
    private SurveyUnitExistencePortStub surveyUnitPort;
    UserService userService;
    DateService dateService = new FixedDateService();
    SurveyUnitRepository surveyUnitRepository;
    QuestionnaireStateClient questionnaireStateClient;
    SurveyUnitClosablePolicy surveyUnitClosablePolicy;


    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        closingCauseRepository = new ClosingCauseRepositoryStub();
        surveyUnitPort = new SurveyUnitExistencePortStub();
        surveyUnitRepository = mock (SurveyUnitRepository.class);
        questionnaireStateClient = mock(QuestionnaireStateClient.class);
        surveyUnitClosablePolicy = new SurveyUnitClosablePolicy();
        surveyUnitClosing = new SurveyUnitClosing(closingCauseRepository, surveyUnitPort, userService, dateService, surveyUnitRepository,questionnaireStateClient, surveyUnitClosablePolicy);
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


    @Test
    void shouldReturnEmptyWhenNoCandidates() {
        when(userService.getUserOUs(any(), anyBoolean()))
                .thenReturn(List.of(new OrganizationUnitDto("OU1","OU1")));

        when(surveyUnitRepository.findClosableCandidates(anyLong(), any()))
                .thenReturn(List.of());

        var presenter = mockPresenter();

        surveyUnitClosing.getSurveyUnitsToClose("user", presenter);

        verify(presenter).empty();
    }

    private <T> SurveyUnitClosingPresenter<T> mockPresenter() {
        return mock(SurveyUnitClosingPresenter.class);
    }

    private ClosableSurveyUnitCandidateView mockCandidate(
            String id,
            StateType state,
            ContactOutcomeType outcome) {

        ClosableSurveyUnitCandidateView mock = mock(ClosableSurveyUnitCandidateView.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getCurrentStateType()).thenReturn(state);
        when(mock.getContactOutcomeType()).thenReturn(outcome);
        return mock;
    }

    private ClosableSurveyUnitView mockProjection(String id) {
        ClosableSurveyUnitView mock = mock(ClosableSurveyUnitView.class);
        when(mock.getId()).thenReturn(id);
        return mock;
    }

    @Test
    void shouldReturnResultsWhenCandidatesExist() {
        // Setup user OUs
        when(userService.getUserOUs(any(), anyBoolean()))
                .thenReturn(List.of(new OrganizationUnitDto("OU1", "OU1")));

        // Setup candidates
        var candidate1 = mockCandidate("SU1", StateType.FIN, ContactOutcomeType.INA);
        var candidate2 = mockCandidate("SU2", StateType.WFT, ContactOutcomeType.REF);
        when(surveyUnitRepository.findClosableCandidates(anyLong(), any()))
                .thenReturn(List.of(candidate1, candidate2));

        // Setup questionnaire states
        InterrogationOkNokDto interrogationOkNokDto = new InterrogationOkNokDto(
                List.of(),
                List.of(new InterrogationOkNokResponseDto("SU1"))
        );
        when(questionnaireStateClient.getQuestionnairesStateFromDataCollection(any()))
                .thenReturn(ResponseEntity.ok(interrogationOkNokDto));

        // Setup projections
        var projection1 = mockProjection("SU1");
        var projection2 = mockProjection("SU2");
        when(surveyUnitRepository.findClosableSurveyUnits(any()))
                .thenReturn(List.of(projection1, projection2));

        var presenter = mockPresenter();

        surveyUnitClosing.getSurveyUnitsToClose("user", presenter);

        verify(presenter).present(anyList(), any(), any());
    }

    @Test
    void shouldHandleQuestionnaireStateClientError() {
        // Setup user OUs
        when(userService.getUserOUs(any(), anyBoolean()))
                .thenReturn(List.of(new OrganizationUnitDto("OU1", "OU1")));

        // Setup candidates
        var candidate = mockCandidate("SU", StateType.FIN, ContactOutcomeType.INA);
        when(surveyUnitRepository.findClosableCandidates(anyLong(), any()))
                .thenReturn(List.of(candidate));

        // Setup questionnaire state client error
        when(questionnaireStateClient.getQuestionnairesStateFromDataCollection(any()))
                .thenThrow(new RuntimeException("API error"));

        var presenter = mockPresenter();

        surveyUnitClosing.getSurveyUnitsToClose("user", presenter);

        verify(presenter).present(anyList(), any(), any());
    }
}