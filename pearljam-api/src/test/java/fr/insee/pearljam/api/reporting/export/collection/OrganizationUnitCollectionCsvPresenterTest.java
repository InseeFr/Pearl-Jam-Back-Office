package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationUnitCollectionCsvPresenterTest {

    private final OrganizationUnitCollectionCsvPresenter presenter = new OrganizationUnitCollectionCsvPresenter();

    @Test
    @DisplayName("Returns Country rows when no organization unit stats are provided")
    void shouldReturnEmptyRows_whenNoStats() {
        // Given / When
        OrganizationUnitCollectionCsv csv = presenter.present(List.of(), new CampaignDailyStats());

        // Then
        List<String> valuesTotalCountry = csv.rows().getFirst().values();
        assertThat(valuesTotalCountry.getFirst()).isEqualTo(CollectionCsvRow.TOTAL_FRANCE);
        assertThat(valuesTotalCountry.getLast()).isEqualTo("0");
    }

    @Test
    @DisplayName("Maps organization unit stats to rows preserving the input order and the OU label")
    void shouldMapStatsPreservingOrder() {
        // Given
        OrganizationUnitDailyStats paris = buildStats("OU-1", "Site Paris");
        OrganizationUnitDailyStats lyon = buildStats("OU-2", "Site Lyon");

        // When
        OrganizationUnitCollectionCsv csv = presenter.present(List.of(paris, lyon), new CampaignDailyStats());

        // Then
        assertThat(csv.rows()).hasSize(3);
        assertThat(csv.rows().get(0).values().getFirst()).isEqualTo("Site Paris");
        assertThat(csv.rows().get(1).values().getFirst()).isEqualTo("Site Lyon");
    }

    @Test
    @DisplayName("Produces rows whose size matches the header size")
    void shouldHaveRowSizeMatchingHeaderSize() {
        // Given
        OrganizationUnitDailyStats stats = buildStats("OU-1", "Site Paris");

        // When
        OrganizationUnitCollectionCsv csv = presenter.present(List.of(stats), new CampaignDailyStats());

        // Then
        assertThat(csv.rows().getFirst().values()).hasSameSizeAs(csv.headers().values());
    }

    private static OrganizationUnitDailyStats buildStats(String id, String label) {
        OrganizationUnitDailyStats stats = new OrganizationUnitDailyStats();
        stats.setOuId(id);
        stats.setOuLabel(label);
        return stats;
    }
}
