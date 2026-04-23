package fr.insee.pearljam.domain.surveyunit.port.in;


public interface SurveyUnitsToClosePort {

    <T> T getSurveyUnitsToClose(String userId, SurveyUnitToCloseStatsPresenter<T> presenter);
}
