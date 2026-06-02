package fr.insee.pearljam.domain.surveyunit.port.in.application;

import org.springframework.data.domain.Pageable;

public interface SurveyUnitCompletedPort {
    <T> T getCompletedSurveyUnits(String campaignId,
                                  String search,
                                  Pageable pageable,
                                  SurveyUnitCompletedPresenter<T> presenter);
}
