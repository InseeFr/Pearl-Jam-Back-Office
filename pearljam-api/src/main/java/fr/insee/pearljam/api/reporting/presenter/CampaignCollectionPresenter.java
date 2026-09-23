package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionItemResponse;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionListResponse;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignCollectionPresenter implements CampaignStatsPresenter<CampaignCollectionListResponse> {

    private long computeMinUpdatedAt(List<CampaignDailyStats> stats) {
        return stats.stream().mapToLong(AbstractDailyStats::getUpdatedAt).min().orElse(0L);
    }

    @Override
    public CampaignCollectionListResponse present(List<CampaignDailyStats> stats) {
        long minUpdatedAt = computeMinUpdatedAt(stats);
        List<CampaignCollectionItemResponse> items = stats.stream()
                .map(this::presentItem)
                .toList();
        return new CampaignCollectionListResponse(items, minUpdatedAt);
    }

    private CampaignCollectionItemResponse presentItem(CampaignDailyStats stats) {
        return new CampaignCollectionItemResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getAllocatedCount(),
                CollectionRatesResponse.from(stats),
                ContactOutcomesProgressResponse.from(stats),
                ClosingCausesProgressResponse.from(stats));
    }

    public CampaignCollectionResponse present(CampaignDailyStats stats) {
        return new CampaignCollectionResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getAllocatedCount(),
                CollectionRatesResponse.from(stats),
                ContactOutcomesProgressResponse.from(stats),
                ClosingCausesProgressResponse.from(stats),
                stats.getUpdatedAt());
    }
}
