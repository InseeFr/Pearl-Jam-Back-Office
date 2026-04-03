package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignSummaryProgress;
import fr.insee.pearljam.domain.reporting.port.in.CampaignSummaryProgressPort;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.progress.StatesSummaryProgress;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CampaignSummaryProgressService implements CampaignSummaryProgressPort {

    private final CampaignRepository campaignRepository;
    private final CampaignDailyStatsRepositoryPort campaignDailyStatsRepository;
    private final UserService userService;
    private final DateService dateService;
    private final Clock clock;

    @Override
    public List<CampaignSummaryProgress> getCampaignSummaryProgress(String userId, LocalDate day) {
        day = defaultDay(day);
        long currentTimestamp = dateService.getCurrentTimestamp();

        List<String> ouIds = userService.getUserOUsModel(userId, true).stream()
                .map(OrganizationUnitSummary::getId)
                .toList();

        List<CampaignWithVisibility> campaigns = campaignRepository
                .findCampaignWithVisibilityByUserAndManagementVisibility(ouIds, userId, currentTimestamp);

        if (campaigns.isEmpty()) {
            log.info("No campaign visible for {}", userId);
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

    private LocalDate defaultDay(LocalDate day) {
        LocalDate now = LocalDate.now(clock);
        if (day == null || day.isAfter(now)) {
            return now;
        }
        return day;
    }
}
