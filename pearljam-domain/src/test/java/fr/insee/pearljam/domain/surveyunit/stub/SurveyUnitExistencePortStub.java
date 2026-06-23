package fr.insee.pearljam.domain.surveyunit.stub;


import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitExistencePort;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SurveyUnitExistencePortStub implements SurveyUnitExistencePort {

    private final Set<String> existingSurveyUnits = new HashSet<>();

    @Override
    public List<String> findExistingIds(List<String> surveyUnitIds) {
        return surveyUnitIds.stream()
                .filter(existingSurveyUnits::contains)
                .toList();
    }

    // Helper method for tests
    public void addExistingSurveyUnit(String surveyUnitId) {
        existingSurveyUnits.add(surveyUnitId);
    }

    public void reset() {
        existingSurveyUnits.clear();
    }
}