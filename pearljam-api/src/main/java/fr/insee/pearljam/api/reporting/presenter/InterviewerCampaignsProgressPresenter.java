package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsProgressResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesInterviewerProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterviewerCampaignsProgressPresenter
        implements InterviewerCampaignsStatsPresenter<List<InterviewerCampaignsProgressResponse>> {

    @Override
    public List<InterviewerCampaignsProgressResponse> present(List<InterviewerCampaignDailyStats> stats) {
        return stats.stream().map(this::present).toList();
    }

    public InterviewerCampaignsProgressResponse present(InterviewerCampaignDailyStats stats) {
        return new InterviewerCampaignsProgressResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getProgressStateRate(),
                StatesInterviewerProgressResponse.from(stats),
                CommunicationsProgressResponse.from(stats));
    }
}
