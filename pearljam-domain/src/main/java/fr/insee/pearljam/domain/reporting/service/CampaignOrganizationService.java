package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationStatsPresenter;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationPort;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignReferentRepository;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
@Service
@AllArgsConstructor
public class CampaignOrganizationService implements CampaignOrganizationPort {

    private final CampaignDailyStatsRepositoryPort campaignDailyStatsRepositoryPort;
    private final CampaignReferentRepository campaignReferentRepository;
    private final CampaignVisibilityPort campaignVisibilityPort;
    private final UserService userService;
    private final DateService dateService;
    private final Clock clock;

    @Override
    public <T> T getCampaignOrganization(String userId, String campaignId, CampaignOrganizationStatsPresenter<T> presenter) {
        LocalDate now = LocalDate.now(clock);
        List<String> userOUIds = getUserOUIds(userId);

        CampaignDailyStats campaignDailyStats = campaignDailyStatsRepositoryPort
                .findCampaignStats(campaignId, now)
                .orElseThrow(CampaignNotFoundExceptionRuntime::new);

        CampaignVisibility campaign = campaignVisibilityPort.getCampaignVisibility(campaignId, userOUIds);
        List<Referent> referents = campaignReferentRepository.getReferents(campaignId);
        List<InterviewerDailyStats> interviewerDailyStats = campaignDailyStatsRepositoryPort
                .getInterviewerStats(campaignId, userOUIds, now);

        List<OrganizationUnitDailyStats> organizationUnitDailyStats = campaignDailyStatsRepositoryPort
                .getOrganizationUnitsStats(userId, now).stream().filter(ou -> userOUIds.contains(ou.getOuId())).toList();

        long totalAllocatedUserOUs = organizationUnitDailyStats.stream()
                .mapToLong(OrganizationUnitDailyStats::getAllocatedCount)
                .sum();

        long totalNotAffectedUserOUs = organizationUnitDailyStats.stream()
                .mapToLong(OrganizationUnitDailyStats::getUnaffectedCount)
                .sum();

        return presenter.present(campaignDailyStats,campaign, referents, interviewerDailyStats, totalAllocatedUserOUs, totalNotAffectedUserOUs, dateService.getCurrentTimestamp());
    }

    private List<String> getUserOUIds(String userId) {
        return userService.getUserOUsModel(userId, true)
                .stream().map(OrganizationUnitSummary::getId).toList();
    }
}