package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.port.in.CampaignService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitFetchPort;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitCompletedView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SurveyUnitFetchService implements SurveyUnitFetchPort {

    private final SurveyUnitRepository surveyUnitRepository;
    private final CampaignRepository campaignRepository;

    @Override
    public List<SurveyUnitCompletedView> getSurveyUnitsByStatesAndCampaignId(List<StateType> stateTypes, String campaignId) {

        if(!campaignRepository.existsById(campaignId))
        {
            throw new CampaignNotFoundExceptionRuntime();
        }

        return surveyUnitRepository.getSurveyUnitsByStatesAndCampaignId(stateTypes, campaignId);
    }
}
