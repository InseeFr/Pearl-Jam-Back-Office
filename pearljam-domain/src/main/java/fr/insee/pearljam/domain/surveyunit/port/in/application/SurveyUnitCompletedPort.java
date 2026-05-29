package fr.insee.pearljam.domain.surveyunit.port.in.application;

public interface SurveyUnitCompletedPort {
    <T> T getCompletedSurveyUnits(String campaignId, SurveyUnitCompletedPresenter<T> presenter);
}
