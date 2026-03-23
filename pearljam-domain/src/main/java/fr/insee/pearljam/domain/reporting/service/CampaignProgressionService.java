package fr.insee.pearljam.domain.reporting.service;
import fr.insee.pearljam.domain.reporting.projection.CampaignProjection;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.reporting.projection.CampaignProgressionProjection;
import fr.insee.pearljam.domain.reporting.projection.StateCountProjection;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.reporting.port.in.CampaignProgression;
import fr.insee.pearljam.domain.reporting.port.out.CampaignProgressionRepository;
import fr.insee.pearljam.domain.surveyunit.model.count.CommunicationRequestCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

        // TODO : rajouter les stream().collect(Collectors.toMap(CommunicationRequestCountProjection::entityId, projection -> projection));
        // TODO : revert/remettre les dto/classes qui ont été renommés mais garder les nouvelles versions


        Map<String, CampaignProjection> campaigns = campaignProgressionRepository.findAllDtoByOuIds(userOrgUnitIds);

        List<String> campaignIds = campaignProgressionRepository.findAllCampaignIdsByOuIds(userOrgUnitIds);

        if (campaignIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, StateCountProjection> stateCountsByCampaign =
                campaignProgressionRepository.stateCountsByCampaign(campaignIds, userOrgUnitIds, dateToUse);

        Map<String, CommunicationRequestCount> commRequestCountsByCampaign =
                campaignProgressionRepository.commRequestCountsByCampaign(campaignIds, userOrgUnitIds, dateToUse);

        return campaignIds.stream()
                .map(id -> {
                    CampaignProjection campaign = campaigns.get(id);
                    StateCountProjection s = stateCountsByCampaign
                            .getOrDefault(id, StateCountProjection.empty(id));
                    CommunicationRequestCount comm = commRequestCountsByCampaign
                            .getOrDefault(id, CommunicationRequestCount.empty(id));

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
                            campaign.getLabel(),
                            progressRate,
                            surveyUnits
                    );
                })
                .toList();
    }
}

