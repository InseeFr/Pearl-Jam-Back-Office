package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDataDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.InterrogationOkNokResponseDto;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToCloseStatsPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStateClient;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class SurveyUnitsToCloseServiceTest {

    private SurveyUnitsToCloseService service;

    @BeforeEach
    void setUp() {
        service = new SurveyUnitsToCloseService(null, null, null, null);
    }

    @ParameterizedTest
    @MethodSource("closableCases")
    void shouldEvaluateClosableCorrectly(
            StateType state,
            ContactOutcomeType outcome,
            String questionnaireState,
            boolean expected) {

        var candidate = mockCandidate(state, outcome);

        boolean result = service.isClosable(candidate, questionnaireState);

        assertEquals(expected, result);
    }

    private ClosableSurveyUnitCandidateView mockCandidate(
            StateType state,
            ContactOutcomeType outcome) {

        ClosableSurveyUnitCandidateView mock = mock(ClosableSurveyUnitCandidateView.class);
        when(mock.getCurrentStateType()).thenReturn(state);
        when(mock.getContactOutcomeType()).thenReturn(outcome);
        return mock;
    }

    private static Stream<Arguments> closableCases() {

            return Stream.of(
                    // ===== NEVER TRANSMITTED =====
                    Arguments.of(StateType.WFT, ContactOutcomeType.REF, "ANY", true),
                    Arguments.of(StateType.INS, ContactOutcomeType.INA, null, true),

                    // ===== CLO =====
                    Arguments.of(StateType.CLO, ContactOutcomeType.INA, null, false),
                    Arguments.of(StateType.CLO, ContactOutcomeType.REF, "UNAVAILABLE", false),

                    // ===== INA + questionnaire missing =====
                    Arguments.of(StateType.FIN, ContactOutcomeType.INA, null, true),

                    // ===== INA + questionnaire unavailable =====
                    Arguments.of(StateType.FIN, ContactOutcomeType.INA, "UNAVAILABLE", true),

                    // ===== INA + questionnaire exists =====
                    Arguments.of(StateType.FIN, ContactOutcomeType.INA, "VALIDATED", false),

                    // ===== NOT INA =====
                    Arguments.of(StateType.FIN, ContactOutcomeType.REF, null, false),
                    Arguments.of(StateType.TBR, ContactOutcomeType.REF, "UNAVAILABLE", false),

                    // ===== EDGE CASES =====
                    Arguments.of(null, ContactOutcomeType.INA, null, false),
                    Arguments.of(null, ContactOutcomeType.REF, "UNAVAILABLE", false)
            );
    }
}