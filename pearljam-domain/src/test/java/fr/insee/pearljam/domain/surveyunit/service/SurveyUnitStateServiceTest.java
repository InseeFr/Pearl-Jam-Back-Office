package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.service.exception.ForbiddenOperation;
import fr.insee.pearljam.domain.surveyunit.service.exception.StateNotFoundException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.domain.surveyunit.stub.StateRepositoryStub;
import fr.insee.pearljam.domain.surveyunit.stub.SurveyUnitClosingPortStub;
import fr.insee.pearljam.domain.surveyunit.stub.SurveyUnitExistencePortStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SurveyUnitStateServiceTest {

    private SurveyUnitUpdateStateService service;
    private SurveyUnitExistencePortStub surveyUnitExistencePort;
    private SurveyUnitClosingPortStub surveyUnitClosingPort;
    private StateRepositoryStub stateRepository;

    @BeforeEach
    void init() {
        surveyUnitExistencePort = new SurveyUnitExistencePortStub();
        surveyUnitClosingPort = new SurveyUnitClosingPortStub();
        stateRepository = new StateRepositoryStub();
        CampaignDailyStatsRepositoryPort campaignDailyStatsRepositoryPort = mock(CampaignDailyStatsRepositoryPort.class);
        service = new SurveyUnitUpdateStateService(surveyUnitExistencePort, surveyUnitClosingPort, stateRepository, campaignDailyStatsRepositoryPort);
    }

    // ==================== Multiple Survey Units Tests ====================

    @Test
    @DisplayName("When adding state to multiple survey units with all existing, all states are added")
    void addStateToMultipleSurveyUnits_allExist_happyPath() {
        // Given
        String suId1 = "SU_001";
        String suId2 = "SU_002";
        String suId3 = "SU_003";

        surveyUnitExistencePort.addExistingSurveyUnit(suId1);
        surveyUnitExistencePort.addExistingSurveyUnit(suId2);
        surveyUnitExistencePort.addExistingSurveyUnit(suId3);

        stateRepository.setStateForSurveyUnit(suId1, StateType.NNS);
        stateRepository.setStateForSurveyUnit(suId2, StateType.NNS);
        stateRepository.setStateForSurveyUnit(suId3, StateType.NNS);

        // When
        service.addStateToMultipleSurveyUnits(List.of(suId1, suId2, suId3), StateType.ANV);

        // Then
        assertThat(stateRepository.getSavedStatesForSurveyUnit(suId1)).contains(StateType.ANV);
        assertThat(stateRepository.getSavedStatesForSurveyUnit(suId2)).contains(StateType.ANV);
        assertThat(stateRepository.getSavedStatesForSurveyUnit(suId3)).contains(StateType.ANV);
    }

    @ParameterizedTest
    @MethodSource("provideMissingSurveyUnitScenarios")
    @DisplayName("When adding state to survey units with missing units, throws SurveyUnitNotFoundException")
    void addStateToMultipleSurveyUnits_withMissing_throwsException(
            List<String> existingIds,
            List<String> missingIds) {
        // Given
        existingIds.forEach(surveyUnitExistencePort::addExistingSurveyUnit);
        existingIds.forEach(id -> stateRepository.setStateForSurveyUnit(id, StateType.NNS));

        List<String> allIds = new ArrayList<>();
        allIds.addAll(existingIds);
        allIds.addAll(missingIds);

        // When/Then
        assertThatThrownBy(() -> service.addStateToMultipleSurveyUnits(allIds, StateType.ANV))
                .isInstanceOf(SurveyUnitNotFoundException.class)
                .satisfies(ex -> missingIds.forEach(id ->
                        assertThat(ex.getMessage()).contains(id)));
    }

    private static Stream<Arguments> provideMissingSurveyUnitScenarios() {
        return Stream.of(
                Arguments.of(List.of("SU_001"), List.of("SU_002", "SU_003")), // some missing
                Arguments.of(List.of(), List.of("SU_001", "SU_002"))          // all missing
        );
    }

    @Test
    @DisplayName("When adding state to empty list of survey units, no states are added")
    void addStateToMultipleSurveyUnits_emptyList_noStatesAdded() {
        // Given
        // Empty list

        // When/Then - should not throw, just do nothing
        assertThatNoException().isThrownBy(() -> service.addStateToMultipleSurveyUnits(List.of(), StateType.ANV));
    }

    // ==================== Single Survey Unit Tests ====================

    @Test
    @DisplayName("When adding state to survey unit with valid transition, state is saved")
    void addStateToSurveyUnit_validTransition_happyPath() {
        // Given
        String suId = "SU_001";
        stateRepository.setStateForSurveyUnit(suId, StateType.NNS);

        // When
        service.addStateToSurveyUnit(suId, StateType.ANV);

        // Then
        assertThat(stateRepository.getSavedStatesForSurveyUnit(suId)).contains(StateType.ANV);
    }

    @Test
    @DisplayName("When adding state to survey unit with no current state, throws StateNotFoundException")
    void addStateToSurveyUnit_noState_throwsException() {
        // Given
        String suId = "SU_001";
        // No state set

        // When/Then
        assertThatThrownBy(() -> service.addStateToSurveyUnit(suId, StateType.ANV))
                .isInstanceOf(StateNotFoundException.class)
                .hasMessageContaining(suId);
    }

    @Test
    @DisplayName("When adding state to survey unit with invalid transition, throws ForbiddenOperation")
    void addStateToSurveyUnit_invalidTransition_throwsException() {
        // Given
        String suId = "SU_001";
        stateRepository.setStateForSurveyUnit(suId, StateType.NVA);

        // When/Then - NVA to NVA is not allowed
        assertThatThrownBy(() -> service.addStateToSurveyUnit(suId, StateType.NVA))
                .isInstanceOf(ForbiddenOperation.class)
                .hasMessageContaining("does not respect business rules");
    }

    @Test
    @DisplayName("When adding FIN state to survey unit, closing cause is deleted")
    void addStateToSurveyUnit_tbrState_deletesClosingCause() {
        // Given
        String suId = "SU_001";
        stateRepository.setStateForSurveyUnit(suId, StateType.TBR);

        // When
        service.addStateToSurveyUnit(suId, StateType.FIN);

        // Then
        assertThat(stateRepository.getSavedStatesForSurveyUnit(suId)).contains(StateType.FIN);
        assertThat(surveyUnitClosingPort.wasClosingCauseDeletedFor(suId)).isTrue();
    }

    @Test
    @DisplayName("When adding non-TBR/FIN state to survey unit, closing cause is not deleted")
    void addStateToSurveyUnit_nonTbrFinState_noClosingCauseDeletion() {
        // Given
        String suId = "SU_001";
        stateRepository.setStateForSurveyUnit(suId, StateType.NNS);

        // When
        service.addStateToSurveyUnit(suId, StateType.ANV);

        // Then
        assertThat(stateRepository.getSavedStatesForSurveyUnit(suId)).contains(StateType.ANV);
        assertThat(surveyUnitClosingPort.wasClosingCauseDeletedFor(suId)).isFalse();
    }

    // ==================== Valid State Transition Tests ====================

    @ParameterizedTest
    @MethodSource("provideValidTransitions")
    @DisplayName("When transitioning between valid states, transition is allowed")
    void addStateToSurveyUnit_validTransitions(StateType from, StateType to, boolean shouldDeleteClosingCause) {
        // Given
        String suId = "SU_001";
        stateRepository.setStateForSurveyUnit(suId, from);

        // When
        service.addStateToSurveyUnit(suId, to);

        // Then
        assertThat(stateRepository.getSavedStatesForSurveyUnit(suId)).contains(to);
        if (shouldDeleteClosingCause) {
            assertThat(surveyUnitClosingPort.wasClosingCauseDeletedFor(suId)).isTrue();
        }
    }

    private static Stream<Arguments> provideValidTransitions() {
        return Stream.of(
                Arguments.of(StateType.NNS, StateType.ANV, false),
                Arguments.of(StateType.NNS, StateType.VIN, false),
                Arguments.of(StateType.ANV, StateType.VIN, false),
                Arguments.of(StateType.TBR, StateType.FIN, true),
                Arguments.of(StateType.FIN, StateType.WFT, false),
                Arguments.of(StateType.TBR, StateType.WFT, false),
                Arguments.of(StateType.ANV, StateType.CLO, false),
                Arguments.of(StateType.NNS, StateType.NVA, false)
        );
    }

    // ==================== Invalid State Transition Tests ====================

    @ParameterizedTest
    @MethodSource("provideInvalidTransitions")
    @DisplayName("When transitioning between invalid states, throws ForbiddenOperation")
    void addStateToSurveyUnit_invalidTransitions(StateType from, StateType to) {
        // Given
        String suId = "SU_001";
        stateRepository.setStateForSurveyUnit(suId, from);

        // When/Then
        assertThatThrownBy(() -> service.addStateToSurveyUnit(suId, to))
                .isInstanceOf(ForbiddenOperation.class);
    }

    private static Stream<Arguments> provideInvalidTransitions() {
        return Stream.of(
                Arguments.of(StateType.NVA, StateType.NVA),
                Arguments.of(StateType.WFT, StateType.VIN),
                Arguments.of(StateType.ANV, StateType.WFT),
                Arguments.of(StateType.WFT, StateType.FIN),
                Arguments.of(StateType.ANV, StateType.TBR),
                Arguments.of(StateType.FIN, StateType.ANV)
        );
    }
}