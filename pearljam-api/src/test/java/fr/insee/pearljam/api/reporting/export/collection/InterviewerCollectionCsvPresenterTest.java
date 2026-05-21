package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerCollectionCsvPresenterTest {

    private final InterviewerCollectionCsvPresenter presenter = new InterviewerCollectionCsvPresenter();

    @Test
    @DisplayName("Returns Unffacted, Site, Country rows when no interviewer stats are provided")
    void shouldReturnEmptyRows_whenNoStats() {
        // Given / When
        InterviewerCollectionCsv csv = presenter.present(List.of(), new CampaignDailyStats(), new CampaignDailyStats());

        // Then
        List<String> valuesUnaffected = csv.rows().getFirst().values();
        assertThat(valuesUnaffected.getFirst()).isEqualTo(CollectionCsvRow.TOTAL_UNAFFECTED);
        assertThat(valuesUnaffected.getLast()).isEqualTo("0");

        List<String> valuesSite = csv.rows().get(1).values();
        assertThat(valuesSite.getFirst()).isEqualTo(CollectionCsvRow.TOTAL_SITE);
        assertThat(valuesSite.getLast()).isEqualTo("0");

        List<String> valuesTotalCountry = csv.rows().get(2).values();
        assertThat(valuesTotalCountry.getFirst()).isEqualTo(CollectionCsvRow.TOTAL_FRANCE);
        assertThat(valuesTotalCountry.getLast()).isEqualTo("0");
    }

    @Test
    @DisplayName("Maps an interviewer to a row whose first column is the concatenated full name")
    void shouldMapInterviewerToRowWithFullName() {
        // Given
        InterviewerDailyStats stats = buildStats("INT1", "Jane", "Doe");

        // When
        InterviewerCollectionCsv csv = presenter.present(List.of(stats), new CampaignDailyStats(), new CampaignDailyStats());

        // Then
        assertThat(csv.rows()).hasSize(4);
        List<String> values = csv.rows().getFirst().values();
        assertThat(values.get(0)).isEqualTo("Jane Doe");
        assertThat(values.get(1)).isEqualTo("INT1");
    }

    @Test
    @DisplayName("Maps multiple interviewers to one row each preserving the input order")
    void shouldMapMultipleInterviewers() {
        // Given
        InterviewerDailyStats jane = buildStats("INT1", "Jane", "Doe");
        InterviewerDailyStats john = buildStats("INT2", "John", "Smith");

        // When
        InterviewerCollectionCsv csv = presenter.present(List.of(jane, john), new CampaignDailyStats(), new CampaignDailyStats());

        // Then
        assertThat(csv.rows()).hasSize(5);
        assertThat(csv.rows().get(0).values().get(0)).isEqualTo("Jane Doe");
        assertThat(csv.rows().get(0).values().get(1)).isEqualTo("INT1");
        assertThat(csv.rows().get(1).values().get(0)).isEqualTo("John Smith");
        assertThat(csv.rows().get(1).values().get(1)).isEqualTo("INT2");
    }

    @Test
    @DisplayName("Produces rows whose size matches the header size")
    void shouldHaveRowSizeMatchingHeaderSize() {
        // Given
        InterviewerDailyStats stats = buildStats("INT1", "Jane", "Doe");

        // When
        InterviewerCollectionCsv csv = presenter.present(List.of(stats), new CampaignDailyStats(), new CampaignDailyStats());

        // Then
        assertThat(csv.rows().getFirst().values()).hasSameSizeAs(csv.headers().values());
    }

    private static InterviewerDailyStats buildStats(String id, String firstName, String lastName) {
        InterviewerDailyStats stats = new InterviewerDailyStats();
        stats.setInterviewerId(id);
        stats.setInterviewerFirstName(firstName);
        stats.setInterviewerLastName(lastName);
        return stats;
    }
}
