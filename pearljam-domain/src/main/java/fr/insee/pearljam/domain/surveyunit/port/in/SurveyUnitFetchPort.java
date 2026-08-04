package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SurveyUnitFetchPort {

    Page<SurveyUnitFetchedByStatesAndCampaignIdView> getSurveyUnitsByStatesAndCampaignId(
                                                                                        String userId,
                                                                                        List<StateType> stateTypes,
                                                                                         String campaignId,
                                                                                         String search,
                                                                                         Pageable pageable);
}
