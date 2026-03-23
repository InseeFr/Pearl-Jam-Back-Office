package fr.insee.pearljam.domain.reporting.service;
import fr.insee.pearljam.domain.reporting.projection.*;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.reporting.port.in.CampaignProgression;
import fr.insee.pearljam.domain.reporting.port.out.CampaignProgressionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CampaignProgressionService implements CampaignProgression {

    private final CampaignProgressionRepository campaignProgressionRepository;
    private final UserService userService;

    public List<CampaignProgressionProjection> getCampaignsProgression(String userId, Instant date) {
        Long dateToUse = date.toEpochMilli();

        List<String> userOrgUnitIds = userService
                .getUserOUs(userId, true)
                .stream().map(OrganizationUnitDto::getId).toList();
        if (userOrgUnitIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, CampaignProjection> campaigns = campaignProgressionRepository.findAllDtoByOuIds(userOrgUnitIds)
                .stream().collect(Collectors.toMap(CampaignProjection::id, campaign -> campaign));

        List<String> campaignIds = new ArrayList<>(campaigns.keySet());

        if (campaignIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, StateCountProjection> stateCountsByCampaign =
                campaignProgressionRepository.findGroupedByCampaign(campaignIds, userOrgUnitIds, dateToUse)
                        .stream()
                        .collect(Collectors.toMap(StateCountProjection::entityId, projection -> projection));


        Map<String, CommunicationRequestCountProjection> commRequestCountsByCampaign =
                campaignProgressionRepository.commRequestCountsByCampaign(campaignIds, userOrgUnitIds, dateToUse)
                        .stream()
                        .collect(Collectors.toMap(CommunicationRequestCountProjection::entityId, projection -> projection));

        return campaignIds.stream()
                .map(id -> {
                    CampaignProjection campaign = campaigns.get(id);
                    StateCountProjection s = stateCountsByCampaign
                            .getOrDefault(id, StateCountProjection.empty(id));
                    CommunicationRequestCountProjection comm = commRequestCountsByCampaign
                            .getOrDefault(id, CommunicationRequestCountProjection.empty(id));

                    long allocated = s.total();

                    CampaignProgressionProjection.SurveyUnits surveyUnits = new CampaignProgressionProjection.SurveyUnits(
                             allocated,
                             s.nnsCount(),
                             s.insCount(),
                             s.wftCount(),
                             s.tbrCount(),
                             s.finCount(),
                             s.prcCount(),
                             s.aocCount(),
                             s.apsCount(),
                             s.finCount(),
                             comm.noticeCount(),
                             comm.reminderCount());

                    float progressRate = (float) (s.finCount() + s.tbrCount()) / allocated * 100;

                    return new CampaignProgressionProjection(
                            id,
                            campaign.label(),
                            progressRate,
                            surveyUnits
                    );
                })
                .toList();
    }
}

