package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.in.CampaignProgressByInterviewersPort;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignProgressByInterviewers;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CommunicationsProgress;
import fr.insee.pearljam.domain.reporting.readmodel.progress.StatesProgress;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignProgressByInterviewersService implements CampaignProgressByInterviewersPort {

    private final UserService userService;
    private final CampaignDailyStatsRepositoryPort campaignDailyStatsRepository;

    @Override
    public CampaignProgressByInterviewers getProgressForDay(String userId, String campaignId, LocalDate day) throws CampaignNotFoundException {
        userService.checkUserAssociationToCampaign(campaignId, userId);

        List<String> userOUIds = userService.getUserOUsModel(userId, false).stream()
                .map(OrganizationUnitSummary::getId)
                .toList();

        List<InterviewerDailyStats> interviewerStats =
                campaignDailyStatsRepository.getInterviewerStats(campaignId, userOUIds, day);

        List<CampaignProgressByInterviewers.Interviewer> interviewersProgress =
                interviewerStats.stream()
                        .map(interviewerDailyStats -> new CampaignProgressByInterviewers.Interviewer(
                                interviewerDailyStats.getInterviewerFirstName() + " " + interviewerDailyStats.getInterviewerLastName(),
                                interviewerDailyStats.getProgressStateRate(),
                                StatesProgress.from(interviewerDailyStats),
                                CommunicationsProgress.from(interviewerDailyStats)))
                        .toList();

        CampaignDailyStats siteStat = campaignDailyStatsRepository
                .findCampaignStatsForOrganizationUnits(campaignId, userOUIds, day)
                .orElse(CampaignDailyStats.empty(campaignId));
        CampaignProgressByInterviewers.OrganizationUnit site = new CampaignProgressByInterviewers.OrganizationUnit(
                siteStat.progressStateRate(),
                StatesProgress.from(siteStat),
                CommunicationsProgress.from(siteStat));

        CampaignDailyStats campaignStats = campaignDailyStatsRepository
                .findCampaignStats(campaignId, day)
                .orElse(CampaignDailyStats.empty(campaignId));
        CampaignProgressByInterviewers.Campaign total = new CampaignProgressByInterviewers.Campaign(
                campaignStats.progressStateRate(),
                StatesProgress.from(campaignStats),
                CommunicationsProgress.from(campaignStats));

        return new CampaignProgressByInterviewers(interviewersProgress, site, total);
    }
}
