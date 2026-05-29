package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignProgressCsvPresenterTest {

    private final CampaignProgressCsvPresenter presenter = new CampaignProgressCsvPresenter();

    @Test
    @DisplayName("Returns empty rows when no stats are provided")
    void shouldReturnEmptyRows_whenNoStats() {
        // Given / When
        CampaignProgressCsv csv = presenter.present(List.of());

        // Then
        assertThat(csv.rows()).isEmpty();
    }

    @Test
    @DisplayName("Maps a single CampaignDailyStats to a row with values in header order")
    void shouldMapStatsToRow() {
        // Given
        CampaignDailyStats stats = buildStats("camp-1", "Enquête 1");

        // When
        CampaignProgressCsv csv = presenter.present(List.of(stats));

        // Then
        assertThat(csv.rows()).hasSize(1);
        List<String> values = csv.rows().getFirst().values();
        assertThat(values.getFirst()).isEqualTo("Enquête 1");
        assertThat(values).hasSize(14);
    }

    @Test
    @DisplayName("Maps multiple stats to rows preserving the input order")
    void shouldMapMultipleStats() {
        // Given
        CampaignDailyStats stats1 = buildStats("camp-1", "Enquête 1");
        CampaignDailyStats stats2 = buildStats("camp-2", "Enquête 2");

        // When
        CampaignProgressCsv csv = presenter.present(List.of(stats1, stats2));

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
        CampaignProgressCsv csv = presenter.present(List.of(stats));

        // Then
        assertThat(csv.rows().getFirst().values()).hasSameSizeAs(csv.headers().values());
    }

    @Test
    @DisplayName("Maps state and communication counts to the row values in expected order")
    void shouldMapStateAndCommunicationCounts() {
        // Given
        CampaignDailyStats stats = new CampaignDailyStats();
        stats.setCampaignId("camp-1");
        stats.setCampaignLabel("Enquête 1");
        stats.setVicStateCount(2);
        stats.setPrcStateCount(7);
        stats.setAocStateCount(8);
        stats.setApsStateCount(9);
        stats.setInsStateCount(1);
        stats.setWftStateCount(4);
        stats.setTbrStateCount(5);
        stats.setFinStateCount(3);
        stats.setCloStateCount(3);
        stats.setNoticeCommunicationCount(11);
        stats.setReminderCommunicationCount(12);

        // When
        CampaignProgressCsv csv = presenter.present(List.of(stats));

        // Then
        List<String> values = csv.rows().getFirst().values();
        assertThat(values).containsExactly(
                "Enquête 1",
                String.valueOf(stats.getProgressStateRate()),
                String.valueOf(stats.getAllocatedCount()),
                "2",
                String.valueOf(stats.getInProgressStateCount()),
                "4", "5",
                String.valueOf(stats.getCompletedStateCount()),
                "7", "8", "9", "1",
                "11", "12"
        );
    }

    private static CampaignDailyStats buildStats(String id, String label) {
        CampaignDailyStats stats = new CampaignDailyStats();
        stats.setCampaignId(id);
        stats.setCampaignLabel(label);
        return stats;
    }
}
