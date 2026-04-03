package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProgressByInterviewersResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CommunicationsProgress;
import fr.insee.pearljam.domain.reporting.readmodel.progress.StatesProgress;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignProgressByInterviewersPresenter implements
        CampaignStatsByInterviewersPresenter<CampaignProgressByInterviewersResponse> {

    @Override
    public CampaignProgressByInterviewersResponse present(List<InterviewerDailyStats> interviewerStats,
                                                          CampaignDailyStats siteStats,
                                                          CampaignDailyStats campaignStats) {
        return new CampaignProgressByInterviewersResponse(
                interviewerStats.stream()
                        .map(interviewer -> new CampaignProgressByInterviewersResponse.Interviewer(
                                interviewer.getInterviewerFirstName() + " " + interviewer.getInterviewerLastName(),
                                interviewer.getProgressStateRate(),
                                StatesProgress.from(interviewer),
                                CommunicationsProgress.from(interviewer)))
                        .toList(),
                new CampaignProgressByInterviewersResponse.OrganizationUnit(
                        siteStats.getProgressStateRate(),
                        StatesProgress.from(siteStats),
                        CommunicationsProgress.from(siteStats)),
                new CampaignProgressByInterviewersResponse.Campaign(
                        campaignStats.getUnaffectedCount(),
                        campaignStats.getProgressStateRate(),
                        StatesProgress.from(campaignStats),
                        CommunicationsProgress.from(campaignStats))
        );
    }
}
