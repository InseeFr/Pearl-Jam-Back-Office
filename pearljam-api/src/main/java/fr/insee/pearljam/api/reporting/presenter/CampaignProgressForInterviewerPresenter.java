package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProgressInterviewerResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesInterviewerProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignProgressForInterviewerPresenter
        implements CampaignStatsPresenter<List<CampaignProgressInterviewerResponse>> {

    @Override
    public List<CampaignProgressInterviewerResponse> present(List<CampaignDailyStats> stats) {
        return stats.stream().map(this::present).toList();
    }

    public CampaignProgressInterviewerResponse present(CampaignDailyStats stats) {
        return new CampaignProgressInterviewerResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getProgressStateRate(),
                StatesInterviewerProgressResponse.from(stats),
                CommunicationsProgressResponse.from(stats));
    }
}
