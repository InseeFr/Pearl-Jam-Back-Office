package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CampaignProgressCsvExporterTest {

    private CampaignProgressCsvExporter exporter;
    private CampaignReportingPort port;

    @BeforeEach
    void setup() {
        port = mock(CampaignReportingPort.class);
        exporter = new CampaignProgressCsvExporter(new CampaignProgressCsvPresenter(), port);
    }

    @Test
    @DisplayName("Returns CSV with headers only when no data is available")
    void shouldReturnCsvWithHeadersOnly_whenNoData() {
        // Given
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(new CampaignProgressCsv(List.of()));

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        // Then
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(1);
        assertThat(lines[0]).contains("Enquête");
    }

    @Test
    @DisplayName("Returns CSV with data rows when campaigns are available")
    void shouldReturnCsvWithDataRows() {
        // Given
        CampaignProgressCsv csv = new CampaignProgressCsv(List.of(
                CsvRow.from("Enquête 1", 75.5f, 10, 2, 3, 4, 5, 6, 7, 8, 9, 1, 11, 12)
        ));
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(csv);

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        // Then
        String csvContent = new String(response.getBody());
        String[] lines = csvContent.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Enquête 1;75.5;");
    }

    @Test
    @DisplayName("Generates filename with user id and date in the Content-Disposition header")
    void shouldGenerateFilenameWithUserIdAndDate() {
        // Given
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(new CampaignProgressCsv(List.of()));

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        // Then
        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("Avancement_enquetes_10062025.csv");
    }

    @Test
    @DisplayName("Returns CSV starting with the UTF-8 BOM")
    void shouldReturnCsvStartingWithBom() {
        // Given
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(new CampaignProgressCsv(List.of()));

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        // Then
        String csv = new String(response.getBody());
        assertThat(csv).startsWith("\uFEFF");
    }

    @Test
    @DisplayName("Returns text/plain content type")
    void shouldReturnTextPlainContentType() {
        // Given
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(new CampaignProgressCsv(List.of()));

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        // Then
        assertThat(response.getHeaders().getContentType()).hasToString("text/plain");
    }
}
