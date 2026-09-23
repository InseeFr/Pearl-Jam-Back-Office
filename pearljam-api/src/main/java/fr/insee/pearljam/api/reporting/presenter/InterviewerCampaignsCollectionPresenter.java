package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.InterviewerCampaignCollectionResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterviewerCampaignsCollectionPresenter
        implements InterviewerCampaignsStatsPresenter<List<InterviewerCampaignCollectionResponse>> {

    private long computeMinUpdatedAt(List<InterviewerCampaignDailyStats> stats) {
        return stats.stream().mapToLong(AbstractDailyStats::getUpdatedAt).min().orElse(0L);
    }

    @Override
    public List<InterviewerCampaignCollectionResponse> present(List<InterviewerCampaignDailyStats> stats) {
        long minUpdatedAt = computeMinUpdatedAt(stats);
        return stats.stream().map(campaignStats -> present(campaignStats, minUpdatedAt)).toList();
    }

    public InterviewerCampaignCollectionResponse present(InterviewerCampaignDailyStats stats, long minUpdatedAt) {
        return new InterviewerCampaignCollectionResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getAllocatedCount(),
                CollectionRatesResponse.from(stats),
                ContactOutcomesProgressResponse.from(stats),
                ClosingCausesProgressResponse.from(stats),
                minUpdatedAt);
    }

    public InterviewerCampaignCollectionResponse present(InterviewerCampaignDailyStats stats) {
        return present(stats, stats.getUpdatedAt());
    }
}
