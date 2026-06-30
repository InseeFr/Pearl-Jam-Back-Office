package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InterviewerCollectionCsvExporterTest {

    private InterviewerCollectionCsvExporter exporter;
    private CampaignReportingByInterviewersPort port;

    @BeforeEach
    void setup()  {
        port = mock(CampaignReportingByInterviewersPort.class);
        when(port.getProgressForDay(any(), any(), any(), any()))
                .thenReturn(new InterviewerCollectionCsv(List.of()));
        exporter = new InterviewerCollectionCsvExporter(new InterviewerCollectionCsvPresenter(), port);
    }

    @Test
    @DisplayName("Returns CSV with data rows when interviewers are available")
    void shouldReturnCsvWithDataRows() throws CampaignNotFoundException {
        // Given
        InterviewerCollectionCsv csv = new InterviewerCollectionCsv(List.of(
                CsvRow.from("Jane Doe", "INT1", 50f, 25f, 10f, 1L, 2L, 3L, 4L, 10L, 5L, 6L, 11L, 100L)
        ));
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(csv);

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, Month.JUNE, 10));

        // Then
        assert response.getBody() != null;
        String csvContent = new String(response.getBody());
        String[] lines = csvContent.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Jane Doe;INT1;50.0;25.0;10.0;");
    }

    @Test
    @DisplayName("Generates filename with campaign id and date in the Content-Disposition header")
    void shouldGenerateFilenameWithCampaignIdAndDate() throws CampaignNotFoundException {
        // Given / When
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, Month.JUNE, 10));

        // Then
        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("camp-1_Avancement_collecte_enqueteurs_10062025.csv");
    }

    @Test
    @DisplayName("Propagates CampaignNotFoundException raised by the port")
    void shouldThrowCampaignNotFoundException_whenCampaignNotFound() {
        // Given
        when(port.getProgressForDay(any(), any(), any(), any())).thenThrow(new CampaignNotFoundExceptionRuntime());

        // When / Then
        LocalDate localDate = LocalDate.of(2025, Month.JUNE, 10);
        assertThatThrownBy(() -> exporter.export("user1", "unknown", localDate))
                .isInstanceOf(CampaignNotFoundExceptionRuntime.class);
    }
}