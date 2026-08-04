package fr.insee.pearljam.domain.surveyunit.port.out;


import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SurveyUnitFetchedByStatesRepositoryPort {

    Page<SurveyUnitFetchedByStatesAndCampaignIdView> getSurveyUnitsByStatesAndCampaignId(
            List<StateType> stateTypes, String campaignId, String search,
            List<String> ouIds, Pageable pageable);
}