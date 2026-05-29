package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitCompletedView;

import java.util.List;

public interface SurveyUnitFetchPort {

    List<SurveyUnitCompletedView> getSurveyUnitsByStatesAndCampaignId(List<StateType> stateTypes, String campaignId);
}
