package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationState;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.model.question.IdentificationQuestionValue;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static fr.insee.pearljam.contracts.constants.Constants.QUESTIONNAIRE_STATE_UNAVAILABLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SurveyUnitClosingViewModelMapperTest {

    SurveyUnitClosingViewModelMapper mapper = new SurveyUnitClosingViewModelMapper();

    ClosableSurveyUnitView surveyUnit = mock(ClosableSurveyUnitView.class);
    ClosableSurveyUnitCandidateView candidate = mock(ClosableSurveyUnitCandidateView.class);

    @Test
    void should_map_scalar_fields() {
        when(surveyUnit.getId()).thenReturn("id1");
        when(surveyUnit.getCampaignLabel()).thenReturn("Campaign");
        when(surveyUnit.getDisplayName()).thenReturn("Display");
        when(surveyUnit.getInterviewerId()).thenReturn("int-01");
        when(surveyUnit.getSsech()).thenReturn(3);

        var result = mapper.map(surveyUnit, Map.of(), Map.of());

        assertEquals("Campaign", result.campaignLabel());
        assertEquals("Display", result.displayName());
        assertEquals("id1", result.id());
        assertEquals("int-01", result.interviewerId());
        assertEquals(3, result.ssech());
    }

    @Test
    void should_return_null_contact_outcome_when_no_candidate() {
        when(surveyUnit.getId()).thenReturn("id1");

        assertNull(mapper.map(surveyUnit, Map.of(), Map.of()).contactOutcome());
    }

    @Test
    void should_return_contact_outcome_from_matching_candidate() {
        when(surveyUnit.getId()).thenReturn("id1");
        when(candidate.getContactOutcomeType()).thenReturn(ContactOutcomeType.INA);

        var result = mapper.map(surveyUnit, Map.of("id1", candidate), Map.of());

        assertEquals(ContactOutcomeType.INA, result.contactOutcome());
    }

    @Test
    void should_return_unavailable_questionnaire_state_when_missing() {
        when(surveyUnit.getId()).thenReturn("id1");

        assertEquals(QUESTIONNAIRE_STATE_UNAVAILABLE, mapper.map(surveyUnit, Map.of(), Map.of()).questionnaireState());
    }

    @Test
    void should_return_questionnaire_state_from_map() {
        when(surveyUnit.getId()).thenReturn("id1");

        assertEquals("COMPLETED", mapper.map(surveyUnit, Map.of(), Map.of("id1", "COMPLETED")).questionnaireState());
    }

    @Test
    void should_map_identification_when_at_least_one_field_is_non_null() {
        when(surveyUnit.getId()).thenReturn("id1");
        when(surveyUnit.getIdentification()).thenReturn(IdentificationQuestionValue.IDENTIFIED);
        when(surveyUnit.getCampaignIdentificationConfiguration()).thenReturn(IdentificationConfiguration.HOUSEF2F);

        var result = mapper.map(surveyUnit, Map.of(), Map.of());

        assertNotNull(result.identificationState());
        assertEquals(IdentificationState.ONGOING, result.identificationState());
    }

}