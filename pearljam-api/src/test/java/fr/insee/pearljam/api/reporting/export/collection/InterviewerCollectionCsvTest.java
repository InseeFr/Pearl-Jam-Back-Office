package fr.insee.pearljam.api.reporting.export.collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerCollectionCsvTest {

    @Test
    @DisplayName("Has the interviewer label and id columns as the first headers")
    void shouldHaveInterviewerLabelThenIdAsFirstHeaders() {
        // Given / When
        InterviewerCollectionCsv csv = new InterviewerCollectionCsv(List.of());

        // Then
        assertThat(csv.headers().values().get(0)).isEqualTo(CollectionCsvHeaders.INTERVIEWER_LABEL.getHeaderName());
        assertThat(csv.headers().values().get(1)).isEqualTo(CollectionCsvHeaders.INTERVIEWER_ID.getHeaderName());
    }

    @Test
    @DisplayName("Exposes all expected collection headers in order")
    void shouldHaveAllExpectedHeadersInOrder() {
        // Given / When
        InterviewerCollectionCsv csv = new InterviewerCollectionCsv(List.of());

        // Then
        assertThat(csv.headers().values()).hasSize(14);
        assertThat(csv.headers().values()).containsExactly(
                CollectionCsvHeaders.INTERVIEWER_LABEL.getHeaderName(),
                CollectionCsvHeaders.INTERVIEWER_ID.getHeaderName(),
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
