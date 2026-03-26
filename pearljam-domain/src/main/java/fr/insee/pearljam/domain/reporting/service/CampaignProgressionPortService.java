package fr.insee.pearljam.domain.reporting.service;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.in.CampaignProgressionPort;
import fr.insee.pearljam.domain.reporting.model.CampaignProgression;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.reporting.readmodel.CommunicationRequestCount;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.reporting.port.out.CampaignProgressionRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.StateCount;
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
public class CampaignProgressionPortService implements CampaignProgressionPort {

    private final CampaignProgressionRepositoryPort campaignProgressionRepositoryPort;
    private final UserService userService;

    public List<CampaignProgression> getCampaignsProgression(String userId, Instant date) {

        List<String> userOrgUnitIds = userService
                .getUserOUsModel(userId, true)
                .stream().map(OrganizationUnitSummary::getId).toList();

        if (userOrgUnitIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, CampaignSummary> campaigns = campaignProgressionRepositoryPort
                .getAllManagedAndNotClosedCampaignsByOrganisationUnits(userOrgUnitIds, date)
                .stream().collect(Collectors.toMap(CampaignSummary::id, campaign -> campaign));
        List<String> campaignIds = new ArrayList<>(campaigns.keySet());


        if (campaigns.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, StateCount> stateCountsByCampaign =
                campaignProgressionRepositoryPort.getStateCountByCampaignsAndOrganisationUnits(campaignIds, userOrgUnitIds, date)
                        .stream()
                        .collect(Collectors.toMap(StateCount::entityId, projection -> projection));


        Map<String, CommunicationRequestCount> commRequestCountsByCampaign =
                campaignProgressionRepositoryPort.getComRequestCountsByCampaignsAndOrganisationUnits(campaignIds, userOrgUnitIds, date)
                        .stream()
                        .collect(Collectors.toMap(CommunicationRequestCount::entityId, projection -> projection));

        return campaignIds.stream()
                .map(id -> {

                    CampaignSummary campaign = campaigns.get(id);

                    StateCount s = stateCountsByCampaign
                            .getOrDefault(id, StateCount.empty(id));
                    CommunicationRequestCount comm = commRequestCountsByCampaign
                            .getOrDefault(id, CommunicationRequestCount.empty(id));

                    long allocated = s.total();

                    CampaignProgression.SurveyUnits surveyUnits = new CampaignProgression.SurveyUnits(
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

                    return new CampaignProgression(
                            id,
                            campaign.label(),
                            progressRate,
                            surveyUnits
                    );
                })
                .toList();
    }
}

