package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewerProgressCsvPresenterTest {

    private final InterviewerProgressCsvPresenter presenter = new InterviewerProgressCsvPresenter();

    @Test
    @DisplayName("Returns Unaffected, Country and Site rows when no interviewer stats are provided")
    void shouldReturnEmptyRows_whenNoStats() {
        // Given / When
        InterviewerProgressCsv csv = presenter.present(List.of(), new CampaignDailyStats(), new CampaignDailyStats());

        // Then
        assertThat(csv.rows()).hasSize(3);
        List<String> valuesUnaffected = csv.rows().getFirst().values();
        assertThat(valuesUnaffected.getFirst()).isEqualTo(ProgressCsvRow.TOTAL_UNAFFECTED);
        assertThat(valuesUnaffected.getLast()).isEmpty();

        List<String> valuesSite = csv.rows().get(1).values();
        assertThat(valuesSite.getFirst()).isEqualTo(ProgressCsvRow.TOTAL_SITE);
        assertThat(valuesSite.getLast()).isEqualTo("0");

        List<String> valuesTotalCountry = csv.rows().get(2).values();
        assertThat(valuesTotalCountry.getFirst()).isEqualTo(ProgressCsvRow.TOTAL_FRANCE);
        assertThat(valuesTotalCountry.getLast()).isEqualTo("0");
    }

    @Test
    @DisplayName("Maps an interviewer to a row whose first column is the concatenated full name")
    void shouldMapInterviewerToRowWithFullName() {
        // Given
        InterviewerDailyStats stats = buildStats("JDUP", "Jean", "Dupont");

        // When
        InterviewerProgressCsv csv = presenter.present(List.of(stats), new CampaignDailyStats(), new CampaignDailyStats());

        // Then
        assertThat(csv.rows()).hasSize(4);
        List<String> values = csv.rows().getFirst().values();
        assertThat(values.get(0)).isEqualTo("Jean Dupont");
        assertThat(values.get(1)).isEqualTo("JDUP");
    }

    @Test
    @DisplayName("Maps multiple interviewers to one row each preserving the input order")
    void shouldMapMultipleInterviewers() {
        // Given
        InterviewerDailyStats jdup = buildStats("JDUP", "Jean", "Dupont");
        InterviewerDailyStats mmar = buildStats("MMAR", "Marie", "Martin");

        // When
        InterviewerProgressCsv csv = presenter.present(List.of(jdup, mmar), new CampaignDailyStats(), new CampaignDailyStats());

        // Then
        assertThat(csv.rows()).hasSize(5);
        assertThat(csv.rows().get(0).values().getFirst()).isEqualTo("Jean Dupont");
        assertThat(csv.rows().get(1).values().getFirst()).isEqualTo("Marie Martin");
    }

    @Test
    @DisplayName("Produces rows whose size matches the header size")
    void shouldHaveRowSizeMatchingHeaderSize() {
        // Given
        InterviewerDailyStats stats = buildStats("JDUP", "Jean", "Dupont");

        // When
        InterviewerProgressCsv csv = presenter.present(List.of(stats), new CampaignDailyStats(), new CampaignDailyStats());

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
