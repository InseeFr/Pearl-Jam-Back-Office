package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InterviewerProgressCsvExporterTest {

    private InterviewerProgressCsvExporter exporter;
    private CampaignReportingByInterviewersPort port;

    @BeforeEach
    void setup() {
        port = mock(CampaignReportingByInterviewersPort.class);
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(new InterviewerProgressCsv(List.of()));
        exporter = new InterviewerProgressCsvExporter(new InterviewerProgressCsvPresenter(), port);
    }

    @Test
    @DisplayName("Returns CSV with headers only when no interviewers are available")
    void shouldReturnCsvWithHeadersOnly_whenNoInterviewers() throws CampaignNotFoundException {
        // Given / When
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        assert response.getBody() != null;
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(1);
        assertThat(lines[0]).contains(ProgressCsvHeaders.INTERVIEWER_LABEL.getHeaderName());
    }

    @Test
    @DisplayName("Returns CSV with data rows when interviewers are available")
    void shouldReturnCsvWithDataRows() throws CampaignNotFoundException {
        // Given
        InterviewerProgressCsv csv = new InterviewerProgressCsv(List.of(
                CsvRow.from("Jean Dupont", "JDUP", 75.5f, 10, 2, 3, 4, 5, 6, 7, 8, 9, 1, 11, 12)
        ));
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(csv);

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        assert response.getBody() != null;
        String csvContent = new String(response.getBody());
        String[] lines = csvContent.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Jean Dupont;JDUP;75.5;");
    }

    @Test
    @DisplayName("Generates filename with campaign id, user id and date in the Content-Disposition header")
    void shouldGenerateFilenameWithUserIdAndDate() throws CampaignNotFoundException {
        // Given / When
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("camp-1_Avancement_enqueteurs_10062025.csv");
    }

    @Test
    @DisplayName("Throws CampaignNotFoundException when campaign does not exist")
    void shouldThrowCampaignNotFoundException_whenCampaignNotFound() {
        // Given
        when(port.getProgressForDay(any(), any(), any(), any())).thenThrow(new CampaignNotFoundExceptionRuntime());

        LocalDate localDate = LocalDate.of(2025, 6, 10);

        // When / Then
        assertThatThrownBy(() -> exporter.export("user1", "unknown", localDate))
                .isInstanceOf(CampaignNotFoundExceptionRuntime.class);
    }
}
