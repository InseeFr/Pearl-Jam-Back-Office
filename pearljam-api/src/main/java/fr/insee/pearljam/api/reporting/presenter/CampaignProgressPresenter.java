package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProgressItemResponse;
import fr.insee.pearljam.api.reporting.response.CampaignProgressListResponse;
import fr.insee.pearljam.api.reporting.response.CampaignProgressResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignProgressPresenter implements CampaignStatsPresenter<CampaignProgressListResponse> {

    private long computeMinUpdatedAt(List<CampaignDailyStats> stats) {
        return stats.stream().mapToLong(AbstractDailyStats::getUpdatedAt).min().orElse(0L);
    }

    @Override
    public CampaignProgressListResponse present(List<CampaignDailyStats> stats) {
        long minUpdatedAt = computeMinUpdatedAt(stats);
        List<CampaignProgressItemResponse> items = stats.stream()
                .map(this::presentItem)
                .toList();
        return new CampaignProgressListResponse(items, minUpdatedAt);
    }

    private CampaignProgressItemResponse presentItem(CampaignDailyStats stats) {
        return new CampaignProgressItemResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getProgressStateRate(),
                StatesProgressResponse.from(stats),
                CommunicationsProgressResponse.from(stats));
    }

    public CampaignProgressResponse present(CampaignDailyStats stats) {
        return new CampaignProgressResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getProgressStateRate(),
                StatesProgressResponse.from(stats),
                CommunicationsProgressResponse.from(stats),
                stats.getUpdatedAt());
    }
}
