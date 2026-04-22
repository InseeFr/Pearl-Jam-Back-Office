package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionInterviewerResponse;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignCollectionForInterviewerPresenterTest {

    private final CampaignCollectionForInterviewerPresenter presenter = new CampaignCollectionForInterviewerPresenter();

    @Test
    void shouldMapCampaignDailyStatsToCollectionInterviewerResponse() {
        CampaignDailyStats stats = ReportingPresenterTestData.campaignStats("camp-1", "Campaign 1", 42L);

        List<CampaignCollectionInterviewerResponse> result = presenter.present(List.of(stats));

        assertThat(result).singleElement().satisfies(response -> {
            assertThat(response.campaignId()).isEqualTo("camp-1");
            assertThat(response.campaignLabel()).isEqualTo("Campaign 1");
            assertThat(response.allocatedInterviewers()).isEqualTo(stats.getAllocatedInterviewersCount());
            assertThat(response.allocatedInterviewers()).isNotEqualTo(stats.getAllocatedCount());
            assertThat(response.rates().collection()).isEqualTo(stats.getCollectionRate());
            assertThat(response.outcomes().total()).isEqualTo(stats.getTotalContactOutcomes());
            assertThat(response.closingCauses().totalClosed()).isEqualTo(stats.getTotalClosingCauses());
        });
    }
}
