package fr.insee.pearljam.api.surveyunit.response;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(name = "SurveyUnitToClose")
public record SurveyUnitToCloseResponse(
        String campaignLabel,
        String surveyUnitId,
        String surveyUnitDisplayName,
        String interviewerLabel,
        Integer ssech,
        String identificationState,
        ContactOutcomeType contactOutcome,
        String questionnaireState,
        ClosingCauseType closingCause
) {}