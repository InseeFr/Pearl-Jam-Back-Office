package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.presenter.CampaignProgressByOrganizationUnitsPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressByOrganizationUnitsResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByOrganizationUnitsPort;
import org.junit.jupiter.api.BeforeEach;
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

    private static final StatesProgressResponse STATES = new StatesProgressResponse(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    private static final CommunicationsProgressResponse COMMUNICATIONS = new CommunicationsProgressResponse(0, 0);

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByOrganizationUnitsPort.class);
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(emptyResponse());
        exporter = new OrganizationUnitProgressCsvExporter(new CampaignProgressByOrganizationUnitsPresenter(), port);
    }

    @Test
    void shouldReturnCsvWithHeadersOnly_whenNoOrganizationUnits() throws CampaignNotFoundException {
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(1);
        assertThat(lines[0]).contains("Site");
    }

    @Test
    void shouldReturnCsvWithDataRows() throws CampaignNotFoundException {
        CampaignProgressByOrganizationUnitsResponse data = new CampaignProgressByOrganizationUnitsResponse(
                List.of(new CampaignProgressByOrganizationUnitsResponse.OrganizationUnit(
                        "Site Paris", 75.5f,
                        new StatesProgressResponse(10, 2, 3, 4, 5, 6, 7, 8, 9, 1),
                        new CommunicationsProgressResponse(11, 12))),
                new CampaignProgressByOrganizationUnitsResponse.Campaign(0f, STATES, COMMUNICATIONS)
        );
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(data);

        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Site Paris;75.5;");
    }

    @Test
    void shouldGenerateFilenameWithUserIdAndDate() throws CampaignNotFoundException {
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("camp-1_Avancement_sites_10062025.csv");
    }

    @Test
    void shouldThrowCampaignNotFoundException_whenCampaignNotFound() throws CampaignNotFoundException {
        when(port.getProgressForDay(any(), any(), any(), any())).thenThrow(new CampaignNotFoundException());

        assertThatThrownBy(() -> exporter.export("user1", "unknown", LocalDate.of(2025, 6, 10)))
                .isInstanceOf(CampaignNotFoundException.class);
    }

    private static CampaignProgressByOrganizationUnitsResponse emptyResponse() {
        return new CampaignProgressByOrganizationUnitsResponse(
                List.of(),
                new CampaignProgressByOrganizationUnitsResponse.Campaign(0f, STATES, COMMUNICATIONS)
        );
    }
}
