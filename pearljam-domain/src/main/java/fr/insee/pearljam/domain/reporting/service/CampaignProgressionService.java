package fr.insee.pearljam.domain.reporting.service;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnit;
import fr.insee.pearljam.domain.reporting.projection.CampaignProgressionProjection;
import fr.insee.pearljam.domain.reporting.query.CampaignQueryResponse;
import fr.insee.pearljam.domain.reporting.query.CommunicationRequestCountQueryResponse;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.reporting.port.in.CampaignProgression;
import fr.insee.pearljam.domain.reporting.port.out.CampaignProgressionRepository;
import fr.insee.pearljam.domain.reporting.query.StateCountQueryResponse;
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
                .getUserOUsModel(userId, true)
                .stream().map(OrganizationUnit::getId).toList();

        if (userOrgUnitIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, CampaignQueryResponse> campaigns = campaignProgressionRepository.getCampaignsByOrganisationUnits(userOrgUnitIds)
                .stream().collect(Collectors.toMap(CampaignQueryResponse::id, campaign -> campaign));
        List<String> campaignIds = new ArrayList<>(campaigns.keySet());


        if (campaigns.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, StateCountQueryResponse> stateCountsByCampaign =
                campaignProgressionRepository.getStateCountByCampaignsAndOrganisationUnits(campaignIds, userOrgUnitIds, dateToUse)
                        .stream()
                        .collect(Collectors.toMap(StateCountQueryResponse::entityId, projection -> projection));


        Map<String, CommunicationRequestCountQueryResponse> commRequestCountsByCampaign =
                campaignProgressionRepository.getComRequestCountsByCampaignsAndOrganisationUnits(campaignIds, userOrgUnitIds, dateToUse)
                        .stream()
                        .collect(Collectors.toMap(CommunicationRequestCountQueryResponse::entityId, projection -> projection));

        return campaignIds.stream()
                .map(id -> {

                    CampaignQueryResponse campaign = campaigns.get(id);

                    StateCountQueryResponse s = stateCountsByCampaign
                            .getOrDefault(id, StateCountQueryResponse.empty(id));
                    CommunicationRequestCountQueryResponse comm = commRequestCountsByCampaign
                            .getOrDefault(id, CommunicationRequestCountQueryResponse.empty(id));

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

