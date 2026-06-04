package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitHistory;

public interface SurveyUnitHistoryRepositoryPort {
    SurveyUnitHistory findSurveyUnitHistory(
            String surveyUnitId);
}
