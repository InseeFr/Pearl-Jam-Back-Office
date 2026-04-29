package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.CampaignProgressResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignProgressCsvTest {

    @Test
    @DisplayName("Has the campaign label column as the first header")
    void shouldHaveCampaignLabelAsFirstHeader() {
        // Given
        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of());

        // When
        CsvRow headers = csv.headers();

        // Then
        assertThat(headers.values().getFirst()).isEqualTo("Enquête");
    }

    @Test
    @DisplayName("Exposes all common progress headers in order after the campaign label")
    void shouldHaveAllCommonHeadersAfterCampaignLabel() {
        // Given
        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of());

        // When
        CsvRow headers = csv.headers();

        // Then
        assertThat(headers.values()).hasSize(14);
        assertThat(headers.values()).containsExactly(
                ProgressCsvHeaders.CAMPAIGN_LABEL.getHeaderName(),
                ProgressCsvHeaders.PROGRESS_RATE.getHeaderName(),
                ProgressCsvHeaders.ALLOCATED.getHeaderName(),
                ProgressCsvHeaders.NOT_STARTED.getHeaderName(),
                ProgressCsvHeaders.IN_PROGRESS.getHeaderName(),
                ProgressCsvHeaders.PENDING_TRANSMISSION.getHeaderName(),
                ProgressCsvHeaders.TO_REVIEW.getHeaderName(),
                ProgressCsvHeaders.VALIDATED.getHeaderName(),
                ProgressCsvHeaders.PREPARING_CONTACT.getHeaderName(),
                ProgressCsvHeaders.WITH_CONTACT.getHeaderName(),
                ProgressCsvHeaders.WITH_APPOINTMENT.getHeaderName(),
                ProgressCsvHeaders.STARTED.getHeaderName(),
                ProgressCsvHeaders.NOTICE_LETTER.getHeaderName(),
                ProgressCsvHeaders.REMINDER_LETTER.getHeaderName()
        );
    }

    @Test
    @DisplayName("Returns empty rows when no responses are provided")
    void shouldReturnEmptyRows_whenNoResponses() {
        // Given
        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of());

        // When / Then
        assertThat(csv.rows()).isEmpty();
    }

    @Test
    @DisplayName("Maps a single response to a row with values in header order")
    void shouldMapResponseToRow() {
        // Given
        CampaignProgressResponse response = new CampaignProgressResponse(
                "camp-1",
                "Enquête 1",
                75.5f,
                new StatesProgressResponse(10, 2, 3, 4, 5, 6, 7, 8, 9, 1),
                new CommunicationsProgressResponse(11, 12)
        );

        // When
        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of(response));

        // Then
        assertThat(csv.rows()).hasSize(1);
        List<String> values = csv.rows().getFirst().values();
        assertThat(values).containsExactly(
                "Enquête 1",
                "75.5",
                "10", "2", "3", "4", "5", "6",
                "7", "8", "9", "1",
                "11", "12"
        );
    }

    @Test
    @DisplayName("Maps multiple responses to rows preserving the input order")
    void shouldMapMultipleResponses() {
        // Given
        CampaignProgressResponse response1 = new CampaignProgressResponse(
                "camp-1", "Enquête 1", 50f,
                new StatesProgressResponse(1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
                new CommunicationsProgressResponse(1, 1)
        );
        CampaignProgressResponse response2 = new CampaignProgressResponse(
                "camp-2", "Enquête 2", 80f,
                new StatesProgressResponse(2, 2, 2, 2, 2, 2, 2, 2, 2, 2),
                new CommunicationsProgressResponse(2, 2)
        );

        // When
        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of(response1, response2));

        // Then
        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values().getFirst()).isEqualTo("Enquête 1");
        assertThat(csv.rows().get(1).values().getFirst()).isEqualTo("Enquête 2");
    }

    @Test
    @DisplayName("Produces rows whose size matches the header size")
    void shouldHaveRowSizeMatchingHeaderSize() {
        // Given
        CampaignProgressResponse response = new CampaignProgressResponse(
                "camp-1", "Enquête 1", 50f,
                new StatesProgressResponse(1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
                new CommunicationsProgressResponse(1, 1)
        );

        // When
        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of(response));

        // Then
        assertThat(csv.rows().getFirst().values()).hasSameSizeAs(csv.headers().values());
    }
}
