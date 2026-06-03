package fr.insee.pearljam.api.surveyunit.response;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitCommunication;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitState;

import java.util.List;

public record SurveyUnitHistoryResponse(String surveyUnitId,
                                        String surveyUnitDisplayName,
                                        List<SurveyUnitState> states,
                                        List<SurveyUnitCommunication> communications) {
}
