package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.readmodel.OrganizationUnitSummary;
import fr.insee.pearljam.domain.reporting.port.in.CampaignProgressionByInterviewersPort;
import fr.insee.pearljam.domain.reporting.port.out.CampaignDailyStatsRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.*;
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
public class CampaignProgressionByInterviewersService implements CampaignProgressionByInterviewersPort {

    private final UserService userService;
    private final CampaignDailyStatsRepositoryPort campaignDailyStatsRepository;

    @Override
    public CampaignProgressionByInterviewers getProgressionForDay(String userId, String campaignId, LocalDate day) throws CampaignNotFoundException {
        userService.checkUserAssociationToCampaign(campaignId, userId);

        List<String> userOUIds = userService.getUserOUsModel(userId, false).stream()
                .map(OrganizationUnitSummary::getId)
                .toList();

        List<InterviewerDailyStats> interviewerStats =
                campaignDailyStatsRepository.getInterviewerStats(campaignId, userOUIds, day);

        List<CampaignProgressionByInterviewers.Interviewer> interviewersProgression =
                interviewerStats.stream()
                        .map(interviewerDailyStats -> new CampaignProgressionByInterviewers.Interviewer(
                                interviewerDailyStats.getInterviewerFirstName() + " " + interviewerDailyStats.getInterviewerLastName(),
                                interviewerDailyStats.getProgressRate(),
                                CampaignProgressionByInterviewers.SurveyUnits.from(interviewerDailyStats)))
                        .toList();

        CampaignDailyStats siteStat = campaignDailyStatsRepository
                .findCampaignStatsForOrganizationUnits(campaignId, userOUIds, day)
                .orElse(CampaignDailyStats.empty(campaignId));
        CampaignProgressionByInterviewers.OrganizationUnit site = new CampaignProgressionByInterviewers.OrganizationUnit(
                siteStat.progressRate(),
                CampaignProgressionByInterviewers.SurveyUnits.from(siteStat));

        CampaignDailyStats campaignStats = campaignDailyStatsRepository
                .findCampaignStats(campaignId, day)
                .orElse(CampaignDailyStats.empty(campaignId));
        CampaignProgressionByInterviewers.Campaign total = new CampaignProgressionByInterviewers.Campaign(
                campaignStats.progressRate(),
                CampaignProgressionByInterviewers.SurveyUnits.from(campaignStats));

        return new CampaignProgressionByInterviewers(interviewersProgression, site, total);
    }
}
