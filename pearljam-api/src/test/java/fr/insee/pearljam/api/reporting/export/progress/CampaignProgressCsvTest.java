package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.CampaignProgressResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignProgressCsvTest {

    @Test
    void shouldHaveCampaignLabelAsFirstHeader() {
        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of());
        CsvRow headers = csv.headers();
        assertThat(headers.values().getFirst()).isEqualTo("Enquête");
    }

    @Test
    void shouldHaveAllCommonHeadersAfterCampaignLabel() {
        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of());
        CsvRow headers = csv.headers();

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
    void shouldReturnEmptyRows_whenNoResponses() {
        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of());
        assertThat(csv.rows()).isEmpty();
    }

    @Test
    void shouldMapResponseToRow() {
        CampaignProgressResponse response = new CampaignProgressResponse(
                "camp-1",
                "Enquête 1",
                75.5f,
                new StatesProgressResponse(10, 2, 3, 4, 5, 6, 7, 8, 9, 1),
                new CommunicationsProgressResponse(11, 12)
        );

        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of(response));

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
    void shouldMapMultipleResponses() {
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

        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of(response1, response2));

        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values().getFirst()).isEqualTo("Enquête 1");
        assertThat(csv.rows().get(1).values().getFirst()).isEqualTo("Enquête 2");
    }

    @Test
    void shouldHaveRowSizeMatchingHeaderSize() {
        CampaignProgressResponse response = new CampaignProgressResponse(
                "camp-1", "Enquête 1", 50f,
                new StatesProgressResponse(1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
                new CommunicationsProgressResponse(1, 1)
        );

        CampaignProgressCsv csv = CampaignProgressCsv.from(List.of(response));

        assertThat(csv.rows().getFirst().values()).hasSameSizeAs(csv.headers().values());
    }
}
