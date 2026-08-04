package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.collection.CollectionCsvHeaders;
import fr.insee.pearljam.api.reporting.export.collection.InterviewerCollectionCsv;
import fr.insee.pearljam.api.reporting.export.collection.InterviewerCollectionCsvExporter;
import fr.insee.pearljam.api.reporting.export.collection.InterviewerCollectionCsvPresenter;
import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
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

class CampaignCollectionByInterviewerExportControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingByInterviewersPort port;

    @BeforeEach
    void setup() {
        port = mock(CampaignReportingByInterviewersPort.class);
        when(port.getProgressForDay(any(), any(), any(), any()))
                .thenReturn(new InterviewerCollectionCsv(List.of()));

        InterviewerCollectionCsvExporter exporter =
                new InterviewerCollectionCsvExporter(new InterviewerCollectionCsvPresenter(), port);
        CampaignCollectionByInterviewerExportController controller =
                new CampaignCollectionByInterviewerExportController(exporter);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_withCsvContentType() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    void shouldReturnAttachmentWithFilename() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("Avancement_collecte_enqueteurs_10062025.csv")));
    }

    @Test
    void shouldReturnCsvWithBomAndHeaders() throws Exception {
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        assertThat(csv).startsWith("\uFEFF")
                .contains(CollectionCsvHeaders.INTERVIEWER_LABEL.getHeaderName())
                .contains(CollectionCsvHeaders.INTERVIEWER_ID.getHeaderName())
                .contains(CollectionCsvHeaders.COLLECTION_RATE.getHeaderName())
                .contains(CollectionCsvHeaders.ALLOCATED_INTERVIEWERS.getHeaderName());
    }

    @Test
    void shouldReturnCsvWithDataRows() throws Exception {
        InterviewerCollectionCsv csv = new InterviewerCollectionCsv(List.of(
                CsvRow.from("Jane Doe", "INT1", 50f, 25f, 10f, 1L, 2L, 3L, 4L, 10L, 5L, 6L, 11L, 100L)
        ));
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(csv);

        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csvContent = new String(content);
        String[] lines = csvContent.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Jane Doe;INT1;50.0;25.0;10.0;");
    }

    @Test
    void shouldReturn404_whenCampaignNotFound() throws Exception {
        when(port.getProgressForDay(any(), any(), any(), any()))
                .thenThrow(new CampaignNotFoundExceptionRuntime());

        mockMvc.perform(get("/api/reporting/campaigns/unknown/interviewers/collection/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_whenDateIsMissing() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection/export"))
                .andExpect(status().isBadRequest());
    }
}
