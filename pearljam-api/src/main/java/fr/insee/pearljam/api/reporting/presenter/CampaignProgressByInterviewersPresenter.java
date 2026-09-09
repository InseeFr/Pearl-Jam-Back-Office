package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProgressByInterviewersResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class CampaignProgressByInterviewersPresenter implements
        CampaignStatsByInterviewersPresenter<CampaignProgressByInterviewersResponse> {

    @Override
    public CampaignProgressByInterviewersResponse present(List<InterviewerDailyStats> interviewerStats,
                                                          CampaignDailyStats siteStats,
                                                          CampaignDailyStats campaignStats) {
        long minUpdatedAt = computeMinUpdatedAt(interviewerStats, siteStats, campaignStats);
        
        return new CampaignProgressByInterviewersResponse(
                interviewerStats.stream()
                        .map(interviewer -> new CampaignProgressByInterviewersResponse.Interviewer(
                                interviewer.getInterviewerFirstName() + " " + interviewer.getInterviewerLastName(),
                                interviewer.getProgressStateRate(),
                                StatesProgressResponse.from(interviewer),
                                CommunicationsProgressResponse.from(interviewer)))
                        .toList(),
                new CampaignProgressByInterviewersResponse.OrganizationUnit(
                        siteStats.getProgressStateRate(),
                        siteStats.getUnaffectedCount(),
                        StatesProgressResponse.from(siteStats),
                        CommunicationsProgressResponse.from(siteStats)),
                new CampaignProgressByInterviewersResponse.Campaign(
                        campaignStats.getProgressStateRate(),
                        StatesProgressResponse.from(campaignStats),
                        CommunicationsProgressResponse.from(campaignStats)),
                minUpdatedAt
        );
    }
    
    private long computeMinUpdatedAt(List<InterviewerDailyStats> interviewerStats, CampaignDailyStats siteStats, CampaignDailyStats campaignStats) {
        return Stream.concat(
                Stream.concat(interviewerStats.stream(), Stream.of(siteStats)),
                Stream.of(campaignStats)
        ).mapToLong(AbstractDailyStats::getUpdatedAt).min().orElse(0L);
    }
}
