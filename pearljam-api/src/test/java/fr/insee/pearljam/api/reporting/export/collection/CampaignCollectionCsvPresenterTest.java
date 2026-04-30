package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignCollectionCsvPresenterTest {

    private final CampaignCollectionCsvPresenter presenter = new CampaignCollectionCsvPresenter();

    @Test
    @DisplayName("Returns empty rows when no stats are provided")
    void shouldReturnEmptyRows_whenNoStats() {
        // Given / When
        CampaignCollectionCsv csv = presenter.present(List.of());

        // Then
        assertThat(csv.rows()).isEmpty();
    }

    @Test
    @DisplayName("Maps stats to rows preserving the campaign label and the input order")
    void shouldMapStatsPreservingOrder() {
        // Given
        CampaignDailyStats stats1 = buildStats("camp-1", "Enquête 1");
        CampaignDailyStats stats2 = buildStats("camp-2", "Enquête 2");

        // When
        CampaignCollectionCsv csv = presenter.present(List.of(stats1, stats2));

        // Then
        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values().getFirst()).isEqualTo("Enquête 1");
        assertThat(csv.rows().get(1).values().getFirst()).isEqualTo("Enquête 2");
    }

    @Test
    @DisplayName("Produces rows whose size matches the header size")
    void shouldHaveRowSizeMatchingHeaderSize() {
        // Given
        CampaignDailyStats stats = buildStats("camp-1", "Enquête 1");

        // When
        CampaignCollectionCsv csv = presenter.present(List.of(stats));

        // Then
        assertThat(csv.rows().getFirst().values()).hasSameSizeAs(csv.headers().values());
    }

    private static CampaignDailyStats buildStats(String id, String label) {
        CampaignDailyStats stats = new CampaignDailyStats();
        stats.setCampaignId(id);
        stats.setCampaignLabel(label);
        return stats;
    }
}
