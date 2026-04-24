package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.InterrogationOkNokDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.InterrogationOkNokResponseDto;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToCloseStatsPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStateClient;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SurveyUnitToCloseServiceTest {

    private SurveyUnitToCloseService service;

    UserService userService;
    DateService dateService = new FixedDateService();
    SurveyUnitRepository surveyUnitRepository;
    QuestionnaireStateClient questionnaireStateClient;
    SurveyUnitToClosePolicy surveyUnitToClosePolicy;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        surveyUnitRepository = mock (SurveyUnitRepository.class);
        questionnaireStateClient = mock(QuestionnaireStateClient.class);
        surveyUnitToClosePolicy = new SurveyUnitToClosePolicy();
        service = new SurveyUnitToCloseService(userService, dateService, surveyUnitRepository,questionnaireStateClient, surveyUnitToClosePolicy);
    }

    @Test
    void shouldReturnEmptyWhenNoCandidates() {
        when(userService.getUserOUs(any(), anyBoolean()))
                .thenReturn(List.of(new OrganizationUnitDto("OU1","OU1")));

        when(surveyUnitRepository.findClosableCandidates(anyLong(), any()))
                .thenReturn(List.of());

        var presenter = mockPresenter();

        service.getSurveyUnitsToClose("user", presenter);

        verify(presenter).empty();
    }

    private <T> SurveyUnitToCloseStatsPresenter<T> mockPresenter() {
        return mock(SurveyUnitToCloseStatsPresenter.class);
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

        service.getSurveyUnitsToClose("user", presenter);

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

        service.getSurveyUnitsToClose("user", presenter);

        verify(presenter).present(anyList(), any(), any());
    }
}
