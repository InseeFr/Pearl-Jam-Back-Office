package fr.insee.pearljam.domain.surveyunit.stub;

import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitPort;

import java.util.HashSet;
import java.util.Set;

public class SurveyUnitPortStub implements SurveyUnitPort {
    private final Set<String> existingSurveyUnits = new HashSet<>();

    public void addExistingSurveyUnit(String surveyUnitId) {
        existingSurveyUnits.add(surveyUnitId);
    }

    @Override
    public boolean existsSurveyUnitById(String surveyUnitId) {
        return existingSurveyUnits.contains(surveyUnitId);
    }
}
