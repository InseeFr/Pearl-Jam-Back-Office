package fr.insee.pearljam.api.surveyunit.response;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(name = "SurveyUnitAssignedResponse")
public record SurveyUnitAssignedResponse(
        String surveyUnitId,
        String surveyUnitDisplayName,
        String interviewerLabel,
        String ssech,
        String location,
        String city,
        StateType questionnaireState,
        ClosingCauseType closingCause
) {}