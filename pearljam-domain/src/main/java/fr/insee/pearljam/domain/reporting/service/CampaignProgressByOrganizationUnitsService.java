package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.in.CampaignProgressByOrganizationUnitsPort;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignProgressByOrganizationUnits;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.OrganizationUnitDailyStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignProgressByOrganizationUnitsService implements CampaignProgressByOrganizationUnitsPort {

    private final UserService userService;
    private final CampaignDailyStatsRepositoryPort campaignDailyStatsRepository;
    private final Clock clock;

    @Override
    public CampaignProgressByOrganizationUnits getProgressForDay(String userId, String campaignId, LocalDate day) throws CampaignNotFoundException {
        day = defaultDay(day);
        userService.checkUserAssociationToCampaign(campaignId, userId);

        List<String> userOUIds = userService.getUserOUsModel(userId, false).stream()
                .map(OrganizationUnitSummary::getId)
                .toList();

        List<OrganizationUnitDailyStats> organizationUnitsStats =
                campaignDailyStatsRepository.getOrganizationUnitsStats(campaignId, userOUIds, day);

        CampaignDailyStats campaignStats = campaignDailyStatsRepository
                .findCampaignStats(campaignId, day)
                .orElse(CampaignDailyStats.empty(campaignId));

        return CampaignProgressByOrganizationUnits.from(organizationUnitsStats, campaignStats);
    }

    private LocalDate defaultDay(LocalDate day) {
        LocalDate now = LocalDate.now(clock);
        if (day == null || day.isAfter(now)) {
            return now;
        }
        return day;
    }
}
