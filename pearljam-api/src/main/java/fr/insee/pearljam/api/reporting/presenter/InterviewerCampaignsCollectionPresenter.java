package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.InterviewerCampaignCollectionResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterviewerCampaignsCollectionPresenter
        implements InterviewerCampaignsStatsPresenter<List<InterviewerCampaignCollectionResponse>> {

    @Override
    public List<InterviewerCampaignCollectionResponse> present(List<InterviewerCampaignDailyStats> stats) {
        return stats.stream().map(this::present).toList();
    }

    public InterviewerCampaignCollectionResponse present(InterviewerCampaignDailyStats stats) {
        return new InterviewerCampaignCollectionResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getAllocatedCount(),
                CollectionRatesResponse.from(stats),
                ContactOutcomesProgressResponse.from(stats),
                ClosingCausesProgressResponse.from(stats),
                stats.getUpdatedAt());
    }
}
