package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.presenter.InterviewerCampaignsProgressPresenter;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesInterviewerProgressResponse;
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

class InterviewerCampaignsProgressCsvExporterTest {

    private InterviewerCampaignsProgressCsvExporter exporter;
    private InterviewerCampaignsReportingPort port;

    @BeforeEach
    void setup() {
        port = mock(InterviewerCampaignsReportingPort.class);
        exporter = new InterviewerCampaignsProgressCsvExporter(new InterviewerCampaignsProgressPresenter(), port);
    }

    @Test
    void shouldReturnCsvWithHeadersOnly_whenNoData() {
        when(port.getCampaignsStatsForInterviewer(any(), any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", "JDUP", LocalDate.of(2025, 6, 10));

        assert response.getBody() != null;
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(1);
        assertThat(lines[0]).contains(ProgressCsvHeaders.CAMPAIGN_LABEL.getHeaderName());
    }

    @Test
    void shouldReturnCsvWithDataRows() {
        InterviewerCampaignsProgressResponse progressResponse = new InterviewerCampaignsProgressResponse(
                "camp-1", "Enquête 1", 75.5f,
                new StatesInterviewerProgressResponse(10, 2, 3, 4, 5, 6, 7, 8, 9, 1),
                new CommunicationsProgressResponse(11, 12)
        );
        when(port.getCampaignsStatsForInterviewer(any(), any(), any(), any())).thenReturn(List.of(progressResponse));

        ResponseEntity<byte[]> response = exporter.export("user1", "JDUP", LocalDate.of(2025, 6, 10));

        assert response.getBody() != null;
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Enquête 1;75.5;");
    }

    @Test
    void shouldGenerateFilenameWithInterviewerIdAndDate() {
        when(port.getCampaignsStatsForInterviewer(any(), any(), any(), any())).thenReturn(List.of());

        ResponseEntity<byte[]> response = exporter.export("user1", "JDUP", LocalDate.of(2025, 6, 10));

        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("JDUP_Avancement_10062025.csv");
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
