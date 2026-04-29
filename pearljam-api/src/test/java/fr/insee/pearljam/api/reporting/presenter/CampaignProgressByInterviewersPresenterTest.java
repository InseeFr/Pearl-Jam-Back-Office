package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProgressByInterviewersResponse;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignProgressByInterviewersPresenterTest {

    private final CampaignProgressByInterviewersPresenter presenter = new CampaignProgressByInterviewersPresenter();

    @Test
    @DisplayName("Maps interviewer and campaign stats to progress response")
    void shouldMapInterviewerAndCampaignStatsToProgressResponse() {
        // Given
        InterviewerDailyStats interviewerStats = ReportingPresenterTestData.interviewerStats("Jane", "Doe");
        CampaignDailyStats siteStats = ReportingPresenterTestData.campaignStats("camp-1", "Campaign 1", 5L);
        CampaignDailyStats campaignStats = ReportingPresenterTestData.campaignStats("camp-1", "Campaign 1", 42L);

        // When
        CampaignProgressByInterviewersResponse result = presenter.present(List.of(interviewerStats), siteStats, campaignStats);

        // Then
        assertThat(result.interviewers()).singleElement().satisfies(interviewer -> {
            assertThat(interviewer.interviewerLabel()).isEqualTo("Jane Doe");
            assertThat(interviewer.progressRate()).isEqualTo(interviewerStats.getProgressStateRate());
            assertThat(interviewer.states().toReview()).isEqualTo(interviewerStats.getTbrStateCount());
            assertThat(interviewer.communications().reminderLetter()).isEqualTo(interviewerStats.getReminderCommunicationCount());
        });
        assertThat(result.site().progressRate()).isEqualTo(siteStats.getProgressStateRate());
        assertThat(result.site().states().allocated()).isEqualTo(siteStats.getAllocatedCount());
        assertThat(result.campaign().unaffected()).isEqualTo(42L);
        assertThat(result.campaign().states().validated()).isEqualTo(campaignStats.getCompletedStateCount());
    }
}
