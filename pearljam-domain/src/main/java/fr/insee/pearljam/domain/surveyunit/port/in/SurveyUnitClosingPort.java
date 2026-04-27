package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;

import java.util.List;

public interface SurveyUnitClosingPort {
    void addClosingCauseToMultipleSurveyUnits(List<String> surveyUnitId, ClosingCauseType type);
}
