package fr.insee.pearljam.domain.surveyunit.port.in;

import java.util.List;

public interface SurveyUnitCompletedPort {
    <T> T getCompletedSurveyUnits(String campaignId, SurveyUnitCompletedPresenter<T> presenter);
}
