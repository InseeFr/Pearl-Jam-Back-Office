package fr.insee.pearljam.api.surveyunit.response;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SurveyUnitCompleted")
public record  SurveyUnitCompletedResponse (
      String surveyUnitId,
      String surveyUnitDisplayName,
      String interviewerLabel,
      String endDate,
      ContactOutcomeType contactOutcome,
      ClosingCauseType closingCauseType,
      Boolean viewed,
      String readOnlyUrl,
      String comment
) {}
