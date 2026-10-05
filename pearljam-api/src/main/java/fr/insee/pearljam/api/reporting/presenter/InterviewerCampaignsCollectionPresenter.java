package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.InterviewerCampaignCollectionItemResponse;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignCollectionListResponse;
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
        implements InterviewerCampaignsStatsPresenter<InterviewerCampaignCollectionListResponse> {

    private long computeMinUpdatedAt(List<InterviewerCampaignDailyStats> stats) {
        return stats.stream().mapToLong(AbstractDailyStats::getUpdatedAt).min().orElse(0L);
    }

    @Override
    public InterviewerCampaignCollectionListResponse present(List<InterviewerCampaignDailyStats> stats) {
        long minUpdatedAt = computeMinUpdatedAt(stats);
        List<InterviewerCampaignCollectionItemResponse> items = stats.stream()
                .map(this::presentItem)
                .toList();
        return new InterviewerCampaignCollectionListResponse(items, minUpdatedAt);
    }

    private InterviewerCampaignCollectionItemResponse presentItem(InterviewerCampaignDailyStats stats) {
        return new InterviewerCampaignCollectionItemResponse(
                stats.getCampaignId(),
                stats.getCampaignLabel(),
                stats.getAllocatedCount(),
                CollectionRatesResponse.from(stats),
                ContactOutcomesProgressResponse.from(stats),
                ClosingCausesProgressResponse.from(stats));
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
