package fr.insee.pearljam.domain.surveyunit.port.in;

public interface SurveyUnitHistoryPort {

    <T> T getSurveyUnitHistory (String surveyUnitId, SurveyUnitHistoryPresenter<T> presenter);
}
