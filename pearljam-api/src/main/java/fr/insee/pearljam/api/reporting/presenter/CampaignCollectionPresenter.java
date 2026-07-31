package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignCollectionPresenter implements CampaignStatsPresenter<List<CampaignCollectionResponse>> {

    @Override
    public List<CampaignCollectionResponse> present(List<CampaignDailyStats> stats) {
        return stats.stream().map(this::present).toList();
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
