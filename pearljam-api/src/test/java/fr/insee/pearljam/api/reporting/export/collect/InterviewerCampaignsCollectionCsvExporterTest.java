package fr.insee.pearljam.api.reporting.export.collect;

import fr.insee.pearljam.api.reporting.presenter.InterviewerCampaignsCollectionPresenter;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignCollectionResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsReportingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InterviewerCampaignsCollectionCsvExporterTest {

    private InterviewerCampaignsCollectionCsvExporter exporter;
    private InterviewerCampaignsReportingPort port;

    @BeforeEach
    void setup() {
        port = mock(InterviewerCampaignsReportingPort.class);
        exporter = new InterviewerCampaignsCollectionCsvExporter(
                new InterviewerCampaignsCollectionPresenter(), port);
    }

    @Test
    void shouldReturnCsvWithHeadersOnly_whenNoData() {
        when(port.getCampaignsStatsForInterviewer(any(), any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", "JDUP", LocalDate.of(2025, 6, 10));

        assert response.getBody() != null;
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(1);
        assertThat(lines[0]).contains(CollectCsvHeaders.CAMPAIGN_LABEL.getHeaderName());
    }

    @Test
    void shouldReturnCsvWithDataRows() {
        InterviewerCampaignCollectionResponse data = new InterviewerCampaignCollectionResponse(
                "camp-1", "Enquête 1", 100L,
                new CollectionRatesResponse(50f, 25f, 10f),
                new ContactOutcomesProgressResponse(1L, 2L, 3L, 4L, 10L),
                new ClosingCausesProgressResponse(5L, 6L, 11L)
        );
        when(port.getCampaignsStatsForInterviewer(any(), any(), any(), any())).thenReturn(List.of(data));

        ResponseEntity<byte[]> response = exporter.export("user1", "JDUP", LocalDate.of(2025, 6, 10));

        assert response.getBody() != null;
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Enquête 1;50.0;25.0;10.0;");
    }

    @Test
    void shouldGenerateFilenameWithInterviewerIdAndDate() {
        when(port.getCampaignsStatsForInterviewer(any(), any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", "JDUP", LocalDate.of(2025, 6, 10));

        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("JDUP_Avancement_collecte_10062025.csv");
    }

    @Test
    void shouldReturnCsvStartingWithBom() {
        when(port.getCampaignsStatsForInterviewer(any(), any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", "JDUP", LocalDate.of(2025, 6, 10));

        assert response.getBody() != null;
        String csv = new String(response.getBody());
        assertThat(csv).startsWith("﻿");
    }

    @Test
    void shouldReturnTextPlainContentType() {
        when(port.getCampaignsStatsForInterviewer(any(), any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", "JDUP", LocalDate.of(2025, 6, 10));

        assertThat(response.getHeaders().getContentType()).hasToString("text/plain");
    }
}
