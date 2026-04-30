package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
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

class CampaignCollectionCsvExporterTest {

    private CampaignCollectionCsvExporter exporter;
    private CampaignReportingPort port;

    @BeforeEach
    void setup() {
        port = mock(CampaignReportingPort.class);
        exporter = new CampaignCollectionCsvExporter(new CampaignCollectionCsvPresenter(), port);
    }

    @Test
    @DisplayName("Returns CSV with headers only when no data is available")
    void shouldReturnCsvWithHeadersOnly_whenNoData() {
        // Given
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(new CampaignCollectionCsv(List.of()));

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        // Then
        assert response.getBody() != null;
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(1);
        assertThat(lines[0]).contains("Enquête");
    }

    @Test
    @DisplayName("Returns CSV with data rows when campaigns are available")
    void shouldReturnCsvWithDataRows() {
        // Given
        CampaignCollectionCsv csv = new CampaignCollectionCsv(List.of(
                CsvRow.from("Enquête 1", 50f, 25f, 10f, 1L, 2L, 3L, 4L, 10L, 5L, 6L, 11L, 100L)
        ));
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(csv);

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        // Then
        assert response.getBody() != null;
        String csvContent = new String(response.getBody());
        String[] lines = csvContent.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Enquête 1;50.0;25.0;10.0;");
    }

    @Test
    @DisplayName("Generates filename without prefix in the Content-Disposition header")
    void shouldGenerateFilenameWithoutPrefix() {
        // Given
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(new CampaignCollectionCsv(List.of()));

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        // Then
        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("Avancement_collecte_enquetes_10062025.csv");
    }

    @Test
    @DisplayName("Returns CSV starting with the UTF-8 BOM")
    void shouldReturnCsvStartingWithBom() {
        // Given
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(new CampaignCollectionCsv(List.of()));

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        // Then
        assert response.getBody() != null;
        String csv = new String(response.getBody());
        assertThat(csv).startsWith("\uFEFF");
    }
}
