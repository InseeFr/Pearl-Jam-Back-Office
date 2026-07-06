package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByOrganizationUnitsPort;
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

class OrganizationUnitProgressCsvExporterTest {

    private OrganizationUnitProgressCsvExporter exporter;
    private CampaignReportingByOrganizationUnitsPort port;

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByOrganizationUnitsPort.class);
        when(port.getProgressForDay(any(), any(), any(), any()))
                .thenReturn(new OrganizationUnitProgressCsv(List.of()));
        exporter = new OrganizationUnitProgressCsvExporter(new OrganizationUnitProgressCsvPresenter(), port);
    }

    @Test
    @DisplayName("Returns CSV with headers only when no organization units are available")
    void shouldReturnCsvWithHeadersOnly_whenNoOrganizationUnits() throws CampaignNotFoundException {
        // Given / When
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(1);
        assertThat(lines[0]).contains("Site");
    }

    @Test
    @DisplayName("Returns CSV with data rows when organization units are available")
    void shouldReturnCsvWithDataRows() throws CampaignNotFoundException {
        // Given
        OrganizationUnitProgressCsv csv = new OrganizationUnitProgressCsv(List.of(
                CsvRow.from("Site Paris", 75.5f, 10, 2, 3, 4, 5, 6, 7, 8, 9, 1, 11, 12)
        ));
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(csv);

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        String csvContent = new String(response.getBody());
        String[] lines = csvContent.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Site Paris;75.5;");
    }

    @Test
    @DisplayName("Generates filename with campaign id and date in the Content-Disposition header")
    void shouldGenerateFilenameWithUserIdAndDate() throws CampaignNotFoundException {
        // Given / When
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("camp-1_Avancement_sites_10062025.csv");
    }

    @Test
    @DisplayName("Propagates CampaignNotFoundException raised by the port")
    void shouldThrowCampaignNotFoundException_whenCampaignNotFound() throws CampaignNotFoundException {
        // Given
        when(port.getProgressForDay(any(), any(), any(), any())).thenThrow(new CampaignNotFoundExceptionRuntime());

        LocalDate localDate = LocalDate.of(2025, 6, 10);

        // When / Then
        assertThatThrownBy(() -> exporter.export("user1", "unknown", localDate))
                .isInstanceOf(CampaignNotFoundExceptionRuntime.class);
    }
}
