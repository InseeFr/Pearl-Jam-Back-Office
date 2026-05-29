package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignProgressPresenter implements CampaignStatsPresenter<List<CampaignProgressResponse>> {

    @Override
    public List<CampaignProgressResponse> present(List<CampaignDailyStats> stats) {
        return stats.stream().map(this::present).toList();
    }

    public CampaignProgressResponse present(CampaignDailyStats stats) {
        return new CampaignProgressResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getProgressStateRate(),
                StatesProgressResponse.from(stats),
                CommunicationsProgressResponse.from(stats));
    }
}
