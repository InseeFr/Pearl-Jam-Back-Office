package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsProgressResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesInterviewerProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterviewerCampaignsProgressPresenter implements InterviewerCampaignsStatsPresenter<List<InterviewerCampaignsProgressResponse>> {

    private long computeMinUpdatedAt(List<InterviewerCampaignDailyStats> stats) {
        return stats.stream().mapToLong(AbstractDailyStats::getUpdatedAt).min().orElse(0L);
    }

    @Override
    public List<InterviewerCampaignsProgressResponse> present(List<InterviewerCampaignDailyStats> stats) {
        long minUpdatedAt = computeMinUpdatedAt(stats);
        return stats.stream().map(campaignStats -> present(campaignStats, minUpdatedAt)).toList();
    }

    public InterviewerCampaignsProgressResponse present(InterviewerCampaignDailyStats stats, long minUpdatedAt) {
        return new InterviewerCampaignsProgressResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getProgressStateRate(),
                StatesInterviewerProgressResponse.from(stats),
                CommunicationsProgressResponse.from(stats),
                minUpdatedAt);
    }

    public InterviewerCampaignsProgressResponse present(InterviewerCampaignDailyStats stats) {
        return present(stats, stats.getUpdatedAt());
    }
}
