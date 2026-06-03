package fr.insee.pearljam.domain.surveyunit.readmodel;

import java.util.List;

public record SurveyUnitHistory(

        String surveyUnitId,
        String surveyUnitDisplayName,
        List<SurveyUnitState> states,
        List<SurveyUnitCommunication> communications
){
}
