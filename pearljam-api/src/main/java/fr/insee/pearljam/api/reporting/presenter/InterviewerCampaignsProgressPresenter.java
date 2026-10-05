package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsProgressItemResponse;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsProgressListResponse;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsProgressResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesInterviewerProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterviewerCampaignsProgressPresenter implements InterviewerCampaignsStatsPresenter<InterviewerCampaignsProgressListResponse> {

    private long computeMinUpdatedAt(List<InterviewerCampaignDailyStats> stats) {
        return stats.stream().mapToLong(AbstractDailyStats::getUpdatedAt).min().orElse(0L);
    }

    @Override
    public InterviewerCampaignsProgressListResponse present(List<InterviewerCampaignDailyStats> stats) {
        long minUpdatedAt = computeMinUpdatedAt(stats);
        List<InterviewerCampaignsProgressItemResponse> items = stats.stream()
                .map(this::presentItem)
                .toList();
        return new InterviewerCampaignsProgressListResponse(items, minUpdatedAt);
    }

    private InterviewerCampaignsProgressItemResponse presentItem(InterviewerCampaignDailyStats stats) {
        return new InterviewerCampaignsProgressItemResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getProgressStateRate(),
                StatesInterviewerProgressResponse.from(stats),
                CommunicationsProgressResponse.from(stats));
    }

    public InterviewerCampaignsProgressResponse present(InterviewerCampaignDailyStats stats) {
        return new InterviewerCampaignsProgressResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getProgressStateRate(),
                StatesInterviewerProgressResponse.from(stats),
                CommunicationsProgressResponse.from(stats),
                stats.getUpdatedAt());
    }
}
