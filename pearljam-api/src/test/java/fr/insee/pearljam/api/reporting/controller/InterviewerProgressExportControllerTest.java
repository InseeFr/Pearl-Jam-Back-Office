package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.progress.InterviewerProgressCsvExporter;
import fr.insee.pearljam.api.reporting.export.progress.ProgressCsvHeaders;
import fr.insee.pearljam.api.reporting.presenter.CampaignProgressByInterviewersPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressByInterviewersResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
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

class InterviewerProgressExportControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingByInterviewersPort port;

    private static final StatesProgressResponse STATES = new StatesProgressResponse(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    private static final CommunicationsProgressResponse COMMUNICATIONS = new CommunicationsProgressResponse(0, 0);

    private static final CampaignProgressByInterviewersResponse EMPTY_RESULT = new CampaignProgressByInterviewersResponse(
            List.of(),
            new CampaignProgressByInterviewersResponse.OrganizationUnit(0f, STATES, COMMUNICATIONS),
            new CampaignProgressByInterviewersResponse.Campaign(0, 0f, STATES, COMMUNICATIONS)
    );

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByInterviewersPort.class);
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(EMPTY_RESULT);

        InterviewerProgressCsvExporter exporter =
                new InterviewerProgressCsvExporter(new CampaignProgressByInterviewersPresenter(), port);
        InterviewerProgressExportController controller = new InterviewerProgressExportController(exporter);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_withCsvContentType() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    void shouldReturnAttachmentWithFilename() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("Avancement_enqueteurs_10062025.csv")));
    }

    @Test
    void shouldReturnCsvWithBomAndHeaders() throws Exception {
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        assertThat(csv).startsWith("\uFEFF")
                .contains(ProgressCsvHeaders.INTERVIEWER_LABEL.getHeaderName())
                .contains(ProgressCsvHeaders.PROGRESS_RATE.getHeaderName());
    }

    @Test
    void shouldReturnCsvWithDataRows() throws Exception {
        CampaignProgressByInterviewersResponse response = new CampaignProgressByInterviewersResponse(
                List.of(new CampaignProgressByInterviewersResponse.Interviewer(
                        "JDUP",
                        "Jean Dupont", 75.5f,
                        new StatesProgressResponse(10, 2, 3, 4, 5, 6, 7, 8, 9, 1),
                        new CommunicationsProgressResponse(11, 12))),
                new CampaignProgressByInterviewersResponse.OrganizationUnit(0f, STATES, COMMUNICATIONS),
                new CampaignProgressByInterviewersResponse.Campaign(0, 0f, STATES, COMMUNICATIONS)
        );
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(response);

        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        String csv = new String(content);
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("JDUP;Jean Dupont;75.5;");
    }

    @Test
    void shouldReturn404_whenCampaignNotFound() throws Exception {
        when(port.getProgressForDay(any(), any(), any(), any()))
                .thenThrow(new CampaignNotFoundException());

        mockMvc.perform(get("/api/reporting/campaigns/unknown/interviewers/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_whenDateIsMissing() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress/export"))
                .andExpect(status().isBadRequest());
    }
}
