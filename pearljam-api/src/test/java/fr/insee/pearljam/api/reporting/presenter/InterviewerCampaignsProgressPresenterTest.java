package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsProgressResponse;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerCampaignsProgressPresenterTest {

    private final InterviewerCampaignsProgressPresenter presenter = new InterviewerCampaignsProgressPresenter();

    @Test
    void shouldMapInterviewerCampaignDailyStatsToProgressInterviewerResponse() {
        InterviewerCampaignDailyStats stats = ReportingPresenterTestData.interviewerCampaignStats("camp-1", "Campaign 1");

        List<InterviewerCampaignsProgressResponse> result = presenter.present(List.of(stats));

        assertThat(result).singleElement().satisfies(response -> {
            assertThat(response.campaignId()).isEqualTo("camp-1");
            assertThat(response.campaignLabel()).isEqualTo("Campaign 1");
            assertThat(response.progressRate()).isEqualTo(stats.getProgressStateRate());
            assertThat(response.states().allocatedInterviewers()).isEqualTo(stats.getAllocatedCount());
            assertThat(response.states().toReview()).isEqualTo(stats.getTbrStateCount());
            assertThat(response.states().validated()).isEqualTo(stats.getCompletedStateCount());
            assertThat(response.communications().noticeLetter()).isEqualTo(stats.getNoticeCommunicationCount());
        });
    }
}
