package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignPhase;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignSummaryProgress;
import fr.insee.pearljam.domain.reporting.port.in.CampaignSummaryProgressPort;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.StatesSummaryProgress;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CampaignSummaryProgressService implements CampaignSummaryProgressPort {

    private final CampaignRepository campaignRepository;
    private final CampaignDailyStatsRepositoryPort campaignDailyStatsRepository;
    private final UserService userService;
    private final DateService dateService;

    @Override
    public List<CampaignSummaryProgress> getCampaignSummaryProgression(String userId, LocalDate day) {
        long currentTimestamp = dateService.getCurrentTimestamp();

        List<String> ouIds = userService.getUserOUsModel(userId, true).stream()
                .map(OrganizationUnitSummary::getId)
                .toList();

        List<CampaignWithVisibility> campaigns = campaignRepository
                .findCampaignWithVisibilityByUserAndManagementVisibility(ouIds, userId, currentTimestamp);

        if (campaigns.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> campaignIds = campaigns.stream().map(CampaignWithVisibility::id).toList();

        Map<String, CampaignDailyStats> statsByCampaign = campaignDailyStatsRepository
                .getCampaignsStats(campaignIds, ouIds, day)
                .stream()
                .collect(Collectors.toMap(CampaignDailyStats::getCampaignId, s -> s));

        return campaigns.stream()
                .filter(c -> statsByCampaign.containsKey(c.id()))
                .map(campaign -> {
                    CampaignDailyStats stats = statsByCampaign.get(campaign.id());
                    return new CampaignSummaryProgress(
                            campaign.id(),
                            campaign.label(),
                            campaign.collectionStartDate(),
                            campaign.collectionEndDate(),
                            campaign.endDate(),
                            CampaignPhase.fromDates(
                                    currentTimestamp,
                                    campaign.managementStartDate(),
                                    campaign.collectionStartDate(),
                                    campaign.collectionEndDate(),
                                    campaign.endDate()
                            ),
                            StatesSummaryProgress.from(stats)
                    );
                })
                .toList();
    }
}
