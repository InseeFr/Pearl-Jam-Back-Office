package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProgressInterviewerResponse;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignProgressForInterviewerPresenterTest {

    private final CampaignProgressForInterviewerPresenter presenter = new CampaignProgressForInterviewerPresenter();

    @Test
    void shouldMapCampaignDailyStatsToProgressInterviewerResponse() {
        CampaignDailyStats stats = ReportingPresenterTestData.campaignStats("camp-1", "Campaign 1", 42L);

        List<CampaignProgressInterviewerResponse> result = presenter.present(List.of(stats));

        assertThat(result).singleElement().satisfies(response -> {
            assertThat(response.campaignId()).isEqualTo("camp-1");
            assertThat(response.campaignLabel()).isEqualTo("Campaign 1");
            assertThat(response.progressRate()).isEqualTo(stats.getProgressStateRate());
            assertThat(response.states().allocatedInterviewers()).isEqualTo(stats.getAllocatedInterviewersCount());
            assertThat(response.states().allocatedInterviewers()).isNotEqualTo(stats.getAllocatedCount());
            assertThat(response.states().toReview()).isEqualTo(stats.getTbrStateCount());
            assertThat(response.states().validated()).isEqualTo(stats.getCompletedStateCount());
            assertThat(response.communications().noticeLetter()).isEqualTo(stats.getNoticeCommunicationCount());
        });
    }
}
