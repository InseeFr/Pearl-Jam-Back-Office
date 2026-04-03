package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.collect.ClosingCausesProgress;
import fr.insee.pearljam.domain.reporting.readmodel.collect.CollectionRates;
import fr.insee.pearljam.domain.reporting.readmodel.collect.ContactOutcomesProgress;
import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
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
                stats.getAllocatedStateCount(),
                CollectionRates.from(stats),
                ContactOutcomesProgress.from(stats),
                ClosingCausesProgress.from(stats));
    }
}
