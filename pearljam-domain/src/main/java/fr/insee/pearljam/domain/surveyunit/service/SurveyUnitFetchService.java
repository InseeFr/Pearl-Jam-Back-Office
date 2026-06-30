package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitFetchPort;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitFetchedByStatesRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SurveyUnitFetchService implements SurveyUnitFetchPort {

    private final SurveyUnitFetchedByStatesRepositoryPort surveyUnitFetchedByStatesRepositoryPort;
    private final CampaignRepository campaignRepository;
    private final UserService userService;

    @Override
    public Page<SurveyUnitFetchedByStatesAndCampaignIdView> getSurveyUnitsByStatesAndCampaignId(
                                                                                                String userId,
                                                                                                List<StateType> stateTypes,
                                                                                                String campaignId,
                                                                                                String search,
                                                                                                Instant endDateBefore,
                                                                                                Pageable pageable) {
        if(!campaignRepository.existsById(campaignId))
        {
            throw new CampaignNotFoundExceptionRuntime();
        }

        List<String> ouIds = userService.getUserOUsModel(userId, true).stream()
                .map(OrganizationUnitSummary::getId)
                .toList();

        return surveyUnitFetchedByStatesRepositoryPort
                .getSurveyUnitsByStatesAndCampaignId(stateTypes, campaignId, search, ouIds, endDateBefore, pageable);
    }
}
