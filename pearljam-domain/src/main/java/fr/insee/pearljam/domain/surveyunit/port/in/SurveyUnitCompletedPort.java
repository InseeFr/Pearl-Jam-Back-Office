package fr.insee.pearljam.domain.surveyunit.port.in;

import org.springframework.data.domain.Pageable;

public interface SurveyUnitCompletedPort {
    <T> T getCompletedSurveyUnits(String userId,
                                  String campaignId,
                                  String search,
                                  Pageable pageable,
                                  SurveyUnitCompletedPresenter<T> presenter);
}
