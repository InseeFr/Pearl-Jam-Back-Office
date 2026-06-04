package fr.insee.pearljam.api.surveyunit.response;

import java.util.List;

public record SurveyUnitHistoryResponse(String surveyUnitId,
                                        String surveyUnitDisplayName,
                                        List<SurveyUnitStateResponse> states,
                                        List<SurveyUnitCommunicationResponse> communications) {
}
