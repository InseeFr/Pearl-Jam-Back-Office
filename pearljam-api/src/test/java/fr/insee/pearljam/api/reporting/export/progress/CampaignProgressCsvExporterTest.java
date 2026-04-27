package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.presenter.CampaignProgressPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import org.junit.jupiter.api.BeforeEach;
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
        exporter = new CampaignProgressCsvExporter(new CampaignProgressPresenter(), port);
    }

    @Test
    void shouldReturnCsvWithHeadersOnly_whenNoData() {
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(1);
        assertThat(lines[0]).contains("Enquête");
    }

    @Test
    void shouldReturnCsvWithDataRows() {
        CampaignProgressResponse progressResponse = new CampaignProgressResponse(
                "camp-1", "Enquête 1", 75.5f,
                new StatesProgressResponse(10, 2, 3, 4, 5, 6, 7, 8, 9, 1),
                new CommunicationsProgressResponse(11, 12)
        );
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(List.of(progressResponse));

        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Enquête 1;75.5;");
    }

    @Test
    void shouldGenerateFilenameWithUserIdAndDate() {
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("Avancement_enquetes_10062025.csv");
    }

    @Test
    void shouldReturnCsvStartingWithBom() {
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        String csv = new String(response.getBody());
        assertThat(csv).startsWith("\uFEFF");
    }

    @Test
    void shouldReturnTextPlainContentType() {
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        assertThat(response.getHeaders().getContentType()).hasToString("text/plain");
    }
}
