package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByOrganizationUnitsPort;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByOrganizationUnitsPresenter;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import fr.insee.pearljam.domain.reporting.service.exception.FutureReportingDateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignReportingByOrganizationUnitsService implements CampaignReportingByOrganizationUnitsPort {

    private final UserService userService;
    private final CampaignDailyStatsRepositoryPort campaignDailyStatsRepository;
    private final Clock clock;

    @Override
    public <T> T getProgressForDay(String userId, String campaignId, LocalDate day,
                                   CampaignStatsByOrganizationUnitsPresenter<T> presenter) throws CampaignNotFoundException {
        day = resolveReportingDay(day);
        userService.checkUserAssociationToCampaign(campaignId, userId);

        List<OrganizationUnitDailyStats> organizationUnitsStats =
                campaignDailyStatsRepository.getOrganizationUnitsStats(campaignId, day);

        CampaignDailyStats campaignStats = campaignDailyStatsRepository
                .findCampaignStats(campaignId, day)
                .orElse(CampaignDailyStats.empty(campaignId));

        return presenter.present(organizationUnitsStats, campaignStats);
    }

    private LocalDate resolveReportingDay(LocalDate day) {
        LocalDate now = LocalDate.now(clock);
        if (day == null) {
            return now;
        }
        if (day.isAfter(now)) {
            throw new FutureReportingDateException();
        }
        return day;
    }
}
