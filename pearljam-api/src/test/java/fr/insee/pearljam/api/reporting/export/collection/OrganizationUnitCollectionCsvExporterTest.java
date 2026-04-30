package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionByOrganizationUnitsPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByOrganizationUnitsResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
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

class OrganizationUnitCollectionCsvExporterTest {

    private OrganizationUnitCollectionCsvExporter exporter;
    private CampaignReportingByOrganizationUnitsPort port;

    private static final CollectionRatesResponse RATES = new CollectionRatesResponse(0f, 0f, 0f);
    private static final ContactOutcomesProgressResponse OUTCOMES =
            new ContactOutcomesProgressResponse(0L, 0L, 0L, 0L, 0L);
    private static final ClosingCausesProgressResponse CLOSING_CAUSES =
            new ClosingCausesProgressResponse(0L, 0L, 0L);

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByOrganizationUnitsPort.class);
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(emptyResponse());
        exporter = new OrganizationUnitCollectionCsvExporter(new CampaignCollectionByOrganizationUnitsPresenter(), port);
    }

    @Test
    void shouldReturnCsvWithDataRows() throws CampaignNotFoundException {
        CampaignCollectionByOrganizationUnitsResponse data = new CampaignCollectionByOrganizationUnitsResponse(
                List.of(new CampaignCollectionByOrganizationUnitsResponse.OrganizationUnit(
                        "Site Paris", 100L,
                        new CollectionRatesResponse(50f, 25f, 10f),
                        new ContactOutcomesProgressResponse(1L, 2L, 3L, 4L, 10L),
                        new ClosingCausesProgressResponse(5L, 6L, 11L))),
                new CampaignCollectionByOrganizationUnitsResponse.Campaign(0L, RATES, OUTCOMES, CLOSING_CAUSES)
        );
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(data);

        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        assert response.getBody() != null;
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Site Paris;50.0;25.0;10.0;");
    }

    @Test
    void shouldGenerateFilenameWithCampaignIdAndDate() throws CampaignNotFoundException {
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("camp-1_Avancement_collecte_sites_10062025.csv");
    }

    @Test
    void shouldThrowCampaignNotFoundException_whenCampaignNotFound() throws CampaignNotFoundException {
        when(port.getProgressForDay(any(), any(), any(), any())).thenThrow(new CampaignNotFoundException());

        assertThatThrownBy(() -> exporter.export("user1", "unknown", LocalDate.of(2025, 6, 10)))
                .isInstanceOf(CampaignNotFoundException.class);
    }

    private static CampaignCollectionByOrganizationUnitsResponse emptyResponse() {
        return new CampaignCollectionByOrganizationUnitsResponse(
                List.of(),
                new CampaignCollectionByOrganizationUnitsResponse.Campaign(0L, RATES, OUTCOMES, CLOSING_CAUSES)
        );
    }
}
