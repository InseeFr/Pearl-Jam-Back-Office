package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.InterviewerCampaignCollectionResponse;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerCampaignsCollectionPresenterTest {

    private final InterviewerCampaignsCollectionPresenter presenter = new InterviewerCampaignsCollectionPresenter();

    @Test
    void shouldMapInterviewerCampaignDailyStatsToCollectionInterviewerResponse() {
        InterviewerCampaignDailyStats stats = ReportingPresenterTestData.interviewerCampaignStats("camp-1", "Campaign 1");

        List<InterviewerCampaignCollectionResponse> result = presenter.present(List.of(stats));

        assertThat(result).singleElement().satisfies(response -> {
            assertThat(response.campaignId()).isEqualTo("camp-1");
            assertThat(response.campaignLabel()).isEqualTo("Campaign 1");
            assertThat(response.allocatedInterviewers()).isEqualTo(stats.getAllocatedCount());
            assertThat(response.rates().collection()).isEqualTo(stats.getCollectionRate());
            assertThat(response.outcomes().total()).isEqualTo(stats.getTotalContactOutcomes());
            assertThat(response.closingCauses().totalClosed()).isEqualTo(stats.getTotalClosingCauses());
        });
    }
}
