package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.collection.InterviewerCampaignsCollectionCsv;
import fr.insee.pearljam.api.reporting.export.collection.InterviewerCampaignsCollectionCsvExporter;
import fr.insee.pearljam.api.reporting.export.collection.InterviewerCampaignsCollectionCsvPresenter;
import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsReportingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InterviewerCampaignsCollectionExportControllerTest {

    private MockMvc mockMvc;
    private InterviewerCampaignsReportingPort reportingPort;

    @BeforeEach
    void setup() {
        reportingPort = mock(InterviewerCampaignsReportingPort.class);
        when(reportingPort.getCampaignsStatsForInterviewer(any(), any(), any(), any()))
                .thenReturn(new InterviewerCampaignsCollectionCsv(List.of()));

        InterviewerCampaignsCollectionCsvExporter exporter =
                new InterviewerCampaignsCollectionCsvExporter(
                        new InterviewerCampaignsCollectionCsvPresenter(), reportingPort);
        InterviewerCampaignsCollectionExportController controller =
                new InterviewerCampaignsCollectionExportController(exporter);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_withCsvContentType() throws Exception {
        mockMvc.perform(get("/api/reporting/interviewers/JDUP/campaigns/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    void shouldReturnAttachmentWithInterviewerIdPrefixAndDate() throws Exception {
        mockMvc.perform(get("/api/reporting/interviewers/JDUP/campaigns/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("JDUP_Avancement_collecte_10062025.csv")));
    }

    @Test
    void shouldReturnCsvWithBomAndHeaders() throws Exception {
        byte[] content = mockMvc.perform(get("/api/reporting/interviewers/JDUP/campaigns/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        assertThat(csv).startsWith("\uFEFF")
                .contains("Enquête")
                .contains("Taux de collecte")
                .contains("Confiées Enquêteur");
    }

    @Test
    void shouldReturnCsvWithDataRows() throws Exception {
        InterviewerCampaignsCollectionCsv csv = new InterviewerCampaignsCollectionCsv(List.of(
                CsvRow.from("Enquête Test", 50f, 25f, 10f, 1L, 2L, 3L, 4L, 10L, 5L, 6L, 11L, 100L)
        ));
        when(reportingPort.getCampaignsStatsForInterviewer(any(), any(), any(), any())).thenReturn(csv);

        byte[] content = mockMvc.perform(get("/api/reporting/interviewers/JDUP/campaigns/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csvContent = new String(content);
        String[] lines = csvContent.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Enquête Test;50.0;25.0;10.0;");
    }

    @Test
    void shouldReturnBadRequest_whenDateIsMissing() throws Exception {
        mockMvc.perform(get("/api/reporting/interviewers/JDUP/campaigns/collection/export"))
                .andExpect(status().isBadRequest());
    }
}
