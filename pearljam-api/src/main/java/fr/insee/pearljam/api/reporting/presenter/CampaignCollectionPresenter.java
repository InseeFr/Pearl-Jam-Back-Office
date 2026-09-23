package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignCollectionPresenter implements CampaignStatsPresenter<List<CampaignCollectionResponse>> {

    private long computeMinUpdatedAt(List<CampaignDailyStats> stats) {
        return stats.stream().mapToLong(AbstractDailyStats::getUpdatedAt).min().orElse(0L);
    }

    @Override
    public List<CampaignCollectionResponse> present(List<CampaignDailyStats> stats) {
        long minUpdatedAt = computeMinUpdatedAt(stats);
        return stats.stream().map(campaignStats -> present(campaignStats, minUpdatedAt)).toList();
    }

    public CampaignCollectionResponse present(CampaignDailyStats stats, long minUpdatedAt) {
        return new CampaignCollectionResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getAllocatedCount(),
                CollectionRatesResponse.from(stats),
                ContactOutcomesProgressResponse.from(stats),
                ClosingCausesProgressResponse.from(stats),
                minUpdatedAt);
    }

    public CampaignCollectionResponse present(CampaignDailyStats stats) {
        return present(stats, stats.getUpdatedAt());
    }
}
