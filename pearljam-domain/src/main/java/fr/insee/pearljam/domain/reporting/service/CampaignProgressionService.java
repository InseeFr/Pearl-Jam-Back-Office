package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignProgression;
import fr.insee.pearljam.domain.reporting.port.in.CampaignProgressionPort;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CampaignProgressionService implements CampaignProgressionPort {

    private final CampaignRepository campaignRepository;
    private final CampaignDailyStatsRepositoryPort campaignDailyStatsRepository;
    private final UserService userService;

    @Override
    public List<CampaignProgression> getCampaignsProgression(String userId, LocalDate day) {
        List<String> userOUIds = userService.getUserOUsModel(userId, true)
                .stream().map(OrganizationUnitSummary::getId).toList();

        if (userOUIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<CampaignSummary> campaigns = campaignRepository
                .findAllManagedAndNotClosedCampaignsByOuIds(
                        userOUIds, day.atStartOfDay(ZoneOffset.UTC).toInstant());

        if (campaigns.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> campaignIds = campaigns.stream().map(CampaignSummary::id).toList();

        Map<String, CampaignDailyStats> statsByCampaign = campaignDailyStatsRepository
                .getCampaignsStats(campaignIds, userOUIds, day)
                .stream()
                .collect(Collectors.toMap(CampaignDailyStats::getCampaignId, s -> s));

        return campaigns.stream()
                .filter(campaign -> statsByCampaign.containsKey(campaign.id()))
                .map(campaign -> {
                    CampaignDailyStats campaignDailyStats = statsByCampaign.get(campaign.id());
                    return new CampaignProgression(
                            campaign.id(),
                            campaign.label(),
                            campaignDailyStats.progressRate(),
                            CampaignProgression.SurveyUnits.from(campaignDailyStats)
                    );
                })
                .toList();
    }
}
