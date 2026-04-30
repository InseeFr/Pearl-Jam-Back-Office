package fr.insee.pearljam.api.reporting.export.collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationUnitCollectionCsvTest {

    @Test
    @DisplayName("Has the organization unit label column as the first header")
    void shouldHaveOrganizationUnitLabelAsFirstHeader() {
        // Given / When
        OrganizationUnitCollectionCsv csv = new OrganizationUnitCollectionCsv(List.of());

        // Then
        assertThat(csv.headers().values().getFirst()).isEqualTo("Site");
    }

    @Test
    @DisplayName("Exposes all expected collection headers in order")
    void shouldHaveAllExpectedHeadersInOrder() {
        // Given / When
        OrganizationUnitCollectionCsv csv = new OrganizationUnitCollectionCsv(List.of());

        // Then
        assertThat(csv.headers().values()).hasSize(13);
        assertThat(csv.headers().values()).containsExactly(
                CollectionCsvHeaders.ORGANIZATION_UNIT_LABEL.getHeaderName(),
                CollectionCsvHeaders.COLLECTION_RATE.getHeaderName(),
                CollectionCsvHeaders.WASTE_RATE.getHeaderName(),
                CollectionCsvHeaders.OUT_OF_SCOPE_RATE.getHeaderName(),
                CollectionCsvHeaders.ACCEPTED.getHeaderName(),
                CollectionCsvHeaders.REFUSED.getHeaderName(),
                CollectionCsvHeaders.UNREACHABLE.getHeaderName(),
                CollectionCsvHeaders.OUT_OF_SCOPE.getHeaderName(),
                CollectionCsvHeaders.TOTAL_OUTCOMES.getHeaderName(),
                CollectionCsvHeaders.ABSENCE_INTERVIEWER.getHeaderName(),
                CollectionCsvHeaders.OTHER_REASONS.getHeaderName(),
                CollectionCsvHeaders.TOTAL_CLOSED.getHeaderName(),
                CollectionCsvHeaders.ALLOCATED.getHeaderName()
        );
    }
}
