package fr.insee.pearljam.domain.reporting.port.out;

import fr.insee.pearljam.contracts.campaign.dto.CampaignProjection;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.surveyunit.model.count.ClosingCauseCountProjection;
import fr.insee.pearljam.domain.surveyunit.model.count.CommunicationRequestCountProjection;
import fr.insee.pearljam.domain.surveyunit.model.count.StateCountProjection;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.CommunicationRequestRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CampaignProgressionRepository {

    CampaignRepository campaignRepository;
    StateRepository stateRepository;
    CommunicationRequestRepository communicationRequestRepository;
    ClosingCauseRepository closingCauseRepository;

    public Map<String, CampaignProjection> findAllDtoByOuIds(List<String> userOrgUnitIds)
    {
        return campaignRepository.findAllDtoByOuIds(userOrgUnitIds)
            .stream().collect(Collectors.toMap(CampaignProjection::getId, campaign -> campaign));
    }

    public List<String>  findAllCampaignIdsByOuIds(List<String> userOrgUnitIds) {
        return campaignRepository.findAllCampaignIdsByOuIds(userOrgUnitIds);
    }


    public Map<String, StateCountProjection> stateCountsByCampaign(List<String> campaignIds, List<String> userOrgUnitIds, Long date)
    {
        return stateRepository.findGroupedByCampaign(campaignIds, userOrgUnitIds, date)
                .stream().collect(Collectors.toMap(StateCountProjection::entityId, sc -> sc));
    }

    public Map<String, CommunicationRequestCountProjection> commRequestCountsByCampaign (List<String> campaignIds,
                                                                                         List<String> ouIds,
                                                                                         Long date)
    {
            return communicationRequestRepository.getCommRequestCountByCampaigns(campaignIds, ouIds, date)
                    .stream()
                    .collect(Collectors.toMap(CommunicationRequestCountProjection::entityId, projection -> projection));
    }

    public Map<String, ClosingCauseCountProjection> closingCauseCountsByCampaign (List<String> campaignIds,
                                                                                  List<String> ouIds,
                                                                                  Long date
    )
    {
        return closingCauseRepository .getStateClosedByClosingCauseCountByCampaigns(campaignIds, ouIds, date)
                .stream()
                .collect(Collectors.toMap(ClosingCauseCountProjection::entityId, projection -> projection));

    }
}
