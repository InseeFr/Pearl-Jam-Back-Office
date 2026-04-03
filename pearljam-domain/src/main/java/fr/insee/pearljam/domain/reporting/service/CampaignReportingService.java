package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
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
public class CampaignReportingService implements CampaignReportingPort {

    private final CampaignRepository campaignRepository;
    private final CampaignDailyStatsRepositoryPort campaignDailyStatsRepository;
    private final UserService userService;
    private final Clock clock;

    @Override
    public <T> T getCampaignsStats(String userId, LocalDate day, CampaignStatsPresenter<T> presenter) {
        day = defaultDay(day);
        List<String> userOUIds = userService.getUserOUsModel(userId, true)
                .stream().map(OrganizationUnitSummary::getId).toList();

        if (userOUIds.isEmpty()) {
            return presenter.present(Collections.emptyList());
        }

        List<CampaignSummary> campaigns = campaignRepository
                .findAllManagedAndNotClosedCampaignsByOuIds(
                        userOUIds, day.atStartOfDay(clock.getZone()).toInstant());

        if (campaigns.isEmpty()) {
            log.info("No opened campaigns found for {}", userId);
            return presenter.present(Collections.emptyList());
        }

        List<String> campaignIds = campaigns.stream().map(CampaignSummary::id).toList();

        Map<String, CampaignDailyStats> statsByCampaign = campaignDailyStatsRepository
                .getCampaignsStats(campaignIds, userOUIds, day)
                .stream()
                .collect(Collectors.toMap(CampaignDailyStats::getCampaignId, s -> s));

        List<CampaignDailyStats> stats = campaigns.stream()
                .filter(campaign -> statsByCampaign.containsKey(campaign.id()))
                .map(campaign -> statsByCampaign.get(campaign.id()))
                .toList();
        return presenter.present(stats);
    }

    private LocalDate defaultDay(LocalDate day) {
        LocalDate now = LocalDate.now(clock);
        if (day == null || day.isAfter(now)) {
            return now;
        }
        return day;
    }
}
