package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignProgressCsvTest {

    @Test
    @DisplayName("Has the campaign label column as the first header")
    void shouldHaveCampaignLabelAsFirstHeader() {
        // Given
        CampaignProgressCsv csv = new CampaignProgressCsv(List.of());

        // When
        CsvRow headers = csv.headers();

        // Then
        assertThat(headers.values().getFirst()).isEqualTo("Enquête");
    }

    @Test
    @DisplayName("Exposes all common progress headers in order after the campaign label")
    void shouldHaveAllCommonHeadersAfterCampaignLabel() {
        // Given
        CampaignProgressCsv csv = new CampaignProgressCsv(List.of());

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
}
