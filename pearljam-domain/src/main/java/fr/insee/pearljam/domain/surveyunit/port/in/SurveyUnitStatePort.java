package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.model.StateType;

import java.util.List;

public interface SurveyUnitStatePort {
    void addStateToMultipleSurveyUnits(List<String> surveyUnitIds, StateType state);

    void addStateToSurveyUnit(String surveyUnitId, StateType state);
}
