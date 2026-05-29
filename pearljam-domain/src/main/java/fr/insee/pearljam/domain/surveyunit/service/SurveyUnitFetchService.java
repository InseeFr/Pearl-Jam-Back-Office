package fr.insee.pearljam.domain.surveyunit.service;

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

    @Override
    public List<SurveyUnitCompletedView> getSurveyUnitsByStatesAndCampaignId(List<StateType> stateTypes, String campaignId) {
        return surveyUnitRepository.getSurveyUnitsByStatesAndCampaignId(stateTypes, campaignId);
    }
}
