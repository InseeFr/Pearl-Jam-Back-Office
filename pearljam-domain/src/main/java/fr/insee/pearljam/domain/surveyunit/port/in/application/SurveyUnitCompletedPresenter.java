package fr.insee.pearljam.domain.surveyunit.port.in.application;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import org.springframework.data.domain.Page;

public interface SurveyUnitCompletedPresenter<T> {
    T present(Page<SurveyUnitFetchedByStatesAndCampaignIdView> surveyUnits);
}
