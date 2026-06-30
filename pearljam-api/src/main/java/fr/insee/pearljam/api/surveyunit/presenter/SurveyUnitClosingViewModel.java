package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.domain.surveyunit.model.IdentificationState;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;

public record SurveyUnitClosingViewModel(
        String campaignLabel,
        String displayName,
        String id,
        String interviewerLabel,
        String interviewerId,
        Integer ssech,
        String departement,
        String city,
        IdentificationState identificationState,
        ContactOutcomeType contactOutcome,
        String questionnaireState,
        ClosingCauseType closingCauseType
) {
}