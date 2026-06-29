package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.domain.surveyunit.model.IdentificationState;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static fr.insee.pearljam.contracts.constants.Constants.QUESTIONNAIRE_STATE_UNAVAILABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SurveyUnitClosingApiPresenterTest {

    @Test
    void should_map_projection_and_candidate_correctly() {
        // GIVEN
        ClosableSurveyUnitView projection = mock(ClosableSurveyUnitView.class);
        when(projection.getId()).thenReturn("id1");
        when(projection.getCampaignLabel()).thenReturn("Campaign");
        when(projection.getDisplayName()).thenReturn("Display");
        when(projection.getSsech()).thenReturn(10);
        when(projection.getInterviewerFirstName()).thenReturn("John");
        when(projection.getInterviewerLastName()).thenReturn("Doe");

        ClosableSurveyUnitCandidateView candidate = mock(ClosableSurveyUnitCandidateView.class);
        when(candidate.getCurrentStateType()).thenReturn(StateType.WFT);
        when(candidate.getContactOutcomeType()).thenReturn(ContactOutcomeType.INA);

        Map<String, ClosableSurveyUnitCandidateView> candidates = Map.of("id1", candidate);
        Map<String, String> questionnaireStates = Map.of("id1", "COMPLETED");

        var presenter = new SurveyUnitClosingApiPresenter(new SurveyUnitClosingViewModelMapper());

        // WHEN
        var result = presenter.present(List.of(projection), candidates, questionnaireStates);

        // THEN
        assertEquals(1, result.size());
        var response = result.getFirst();

        assertEquals("Campaign", response.campaignLabel());
        assertEquals("id1", response.surveyUnitId());
        assertEquals("Display", response.surveyUnitDisplayName());
        assertEquals("John Doe", response.interviewerLabel());
        assertEquals(IdentificationState.MISSING.name(), response.identificationState());
        assertEquals(ContactOutcomeType.INA, response.contactOutcome());
        assertEquals("COMPLETED", response.questionnaireState());
    }

    @Test
    void should_handle_null_candidate() {
        // GIVEN
        ClosableSurveyUnitView projection = mock(ClosableSurveyUnitView.class);
        when(projection.getId()).thenReturn("id1");

        Map<String, ClosableSurveyUnitCandidateView> candidates = Map.of(); // empty
        Map<String, String> questionnaireStates = Map.of();

        var presenter = new SurveyUnitClosingApiPresenter(new SurveyUnitClosingViewModelMapper());

        // WHEN
        var result = presenter.present(List.of(projection), candidates, questionnaireStates);

        // THEN
        var response = result.getFirst();

        assertEquals(IdentificationState.MISSING.name(), response.identificationState());
        assertNull(response.contactOutcome());
    }

    @Test
    void should_use_default_questionnaire_state_when_missing() {
        // GIVEN
        ClosableSurveyUnitView projection = mock(ClosableSurveyUnitView.class);
        when(projection.getId()).thenReturn("id1");

        var presenter = new SurveyUnitClosingApiPresenter(new SurveyUnitClosingViewModelMapper());

        // WHEN
        var result = presenter.present(List.of(projection), Map.of(), Map.of());

        // THEN
        assertEquals(QUESTIONNAIRE_STATE_UNAVAILABLE, result.getFirst().questionnaireState());
    }

    @Test
    void should_build_interviewer_label_with_partial_name() {
        ClosableSurveyUnitView projection = mock(ClosableSurveyUnitView.class);
        when(projection.getId()).thenReturn("id1");
        when(projection.getInterviewerFirstName()).thenReturn("John");
        when(projection.getInterviewerLastName()).thenReturn(null);

        var presenter = new SurveyUnitClosingApiPresenter(new SurveyUnitClosingViewModelMapper());

        var result = presenter.present(List.of(projection), Map.of(), Map.of());

        assertEquals("John", result.getFirst().interviewerLabel());
    }
}