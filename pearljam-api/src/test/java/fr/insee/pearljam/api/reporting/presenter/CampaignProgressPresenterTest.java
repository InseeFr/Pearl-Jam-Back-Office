package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProgressResponse;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignProgressPresenterTest {

    private final CampaignProgressPresenter presenter = new CampaignProgressPresenter();

    @Test
    void shouldMapCampaignDailyStatsToProgressResponse() {
        CampaignDailyStats stats = ReportingPresenterTestData.campaignStats("camp-1", "Campaign 1", 99L);

        List<CampaignProgressResponse> result = presenter.present(List.of(stats));

        assertThat(result).singleElement().satisfies(response -> {
            assertThat(response.campaignId()).isEqualTo("camp-1");
            assertThat(response.campaignLabel()).isEqualTo("Campaign 1");
            assertThat(response.progressRate()).isEqualTo(stats.getProgressStateRate());
            assertThat(response.states().allocated()).isEqualTo(stats.getAllocatedStateCount());
            assertThat(response.states().validated()).isEqualTo(stats.getCompletedStateCount());
            assertThat(response.communications().noticeLetter()).isEqualTo(stats.getNoticeCommunicationCount());
        });
    }
}
