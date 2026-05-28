package fr.insee.pearljam.domain.surveyunit.service.model;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;

public record SurveyUnitCompleted (
        String surveyUnitId,
        String surveyUnitDisplayName,
        String interviewerLabel,
        String endDate,
        ContactOutcomeType contactOutcome,
        ClosingCauseType closingCauseType,
        Boolean read,
        String readOnlyUrl,
        String comment
) {
}
