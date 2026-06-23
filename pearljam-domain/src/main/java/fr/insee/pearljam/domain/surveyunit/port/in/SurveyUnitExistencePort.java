package fr.insee.pearljam.domain.surveyunit.port.in;

import java.util.List;

public interface SurveyUnitExistencePort {
    List<String> findExistingIds(List<String> surveyUnitIds);
}
