package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitFetchPort;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SurveyUnitFetchService implements SurveyUnitFetchPort {

    private final SurveyUnitRepository surveyUnitRepository;
    private final CampaignRepository campaignRepository;

    @Override
    public List<SurveyUnitFetchedByStatesAndCampaignIdView> getSurveyUnitsByStatesAndCampaignId(List<StateType> stateTypes, String campaignId) {

        if(!campaignRepository.existsById(campaignId))
        {
            throw new CampaignNotFoundExceptionRuntime();
        }

        return surveyUnitRepository.getSurveyUnitsByStatesAndCampaignId(stateTypes, campaignId);
    }
}
