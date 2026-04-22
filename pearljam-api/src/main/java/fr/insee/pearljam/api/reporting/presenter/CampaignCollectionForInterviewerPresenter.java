package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionInterviewerResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignCollectionForInterviewerPresenter
        implements CampaignStatsPresenter<List<CampaignCollectionInterviewerResponse>> {

    @Override
    public List<CampaignCollectionInterviewerResponse> present(List<CampaignDailyStats> stats) {
        return stats.stream().map(this::present).toList();
    }

    public CampaignCollectionInterviewerResponse present(CampaignDailyStats stats) {
        return new CampaignCollectionInterviewerResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getAllocatedInterviewersCount(),
                CollectionRatesResponse.from(stats),
                ContactOutcomesProgressResponse.from(stats),
                ClosingCausesProgressResponse.from(stats));
    }
}
