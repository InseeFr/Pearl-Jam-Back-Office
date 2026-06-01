package fr.insee.pearljam.domain.surveyunit.port.in.application;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import java.util.List;

public interface SurveyUnitCompletedPresenter<T> {
    T present(List<SurveyUnitFetchedByStatesAndCampaignIdView> surveyUnits);
}
