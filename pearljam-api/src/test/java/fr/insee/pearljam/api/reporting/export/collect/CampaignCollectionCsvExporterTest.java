package fr.insee.pearljam.api.reporting.export.collect;

import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
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

class CampaignCollectionCsvExporterTest {

    private CampaignCollectionCsvExporter exporter;
    private CampaignReportingPort port;

    @BeforeEach
    void setup() {
        port = mock(CampaignReportingPort.class);
        exporter = new CampaignCollectionCsvExporter(new CampaignCollectionPresenter(), port);
    }

    @Test
    void shouldReturnCsvWithHeadersOnly_whenNoData() {
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        assert response.getBody() != null;
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(1);
        assertThat(lines[0]).contains("Enquête");
    }

    @Test
    void shouldReturnCsvWithDataRows() {
        CampaignCollectionResponse data = new CampaignCollectionResponse(
                "camp-1", "Enquête 1", 100L,
                new CollectionRatesResponse(50f, 25f, 10f),
                new ContactOutcomesProgressResponse(1L, 2L, 3L, 4L, 10L),
                new ClosingCausesProgressResponse(5L, 6L, 11L)
        );
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(List.of(data));

        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        assert response.getBody() != null;
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Enquête 1;50.0;25.0;10.0;");
    }

    @Test
    void shouldGenerateFilenameWithoutPrefix() {
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("Avancement_collecte_enquetes_10062025.csv");
    }

    @Test
    void shouldReturnCsvStartingWithBom() {
        when(port.getCampaignsStats(any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", LocalDate.of(2025, 6, 10));

        assert response.getBody() != null;
        String csv = new String(response.getBody());
        assertThat(csv).startsWith("﻿");
    }
}
