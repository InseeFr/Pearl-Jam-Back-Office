package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignCollectionPresenterTest {

    private final CampaignCollectionPresenter presenter = new CampaignCollectionPresenter();

    @Test
    @DisplayName("Maps campaign daily stats to collection response")
    void shouldMapCampaignDailyStatsToCollectionResponse() {
        // Given
        CampaignDailyStats stats = ReportingPresenterTestData.campaignStats("camp-1", "Campaign 1", 99L);

        // When
        List<CampaignCollectionResponse> result = presenter.present(List.of(stats));

        // Then
        assertThat(result).singleElement().satisfies(response -> {
            assertThat(response.campaignId()).isEqualTo("camp-1");
            assertThat(response.campaignLabel()).isEqualTo("Campaign 1");
            assertThat(response.allocated()).isEqualTo(stats.getAllocatedCount());
            assertThat(response.rates().collection()).isEqualTo(stats.getCollectionRate());
            assertThat(response.outcomes().total()).isEqualTo(stats.getTotalContactOutcomes());
            assertThat(response.closingCauses().totalClosed()).isEqualTo(stats.getTotalClosingCauses());
        });
    }
}
