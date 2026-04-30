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
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Returns 200 OK with CSV content type when date is provided")
    void shouldReturnOk_withCsvContentType() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/plain"));
    }

    @Test
    @DisplayName("Returns an attachment whose filename includes the date")
    void shouldReturnAttachmentWithFilename() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("Avancement_enqueteurs_10062025.csv")));
    }

    @Test
    @DisplayName("Returns a CSV starting with the BOM and the expected headers")
    void shouldReturnCsvWithBomAndHeaders() throws Exception {
        // When
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        // Then
        String csv = new String(content);
        assertThat(csv).startsWith("\uFEFF")
                .contains(ProgressCsvHeaders.INTERVIEWER_LABEL.getHeaderName())
                .contains(ProgressCsvHeaders.PROGRESS_RATE.getHeaderName());
    }

    @Test
    @DisplayName("Returns a CSV with one data row per interviewer returned by the port")
    void shouldReturnCsvWithDataRows() throws Exception {
        // Given
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

        // When
        byte[] content = mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        // Then
        String csv = new String(content);
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("Jean Dupont;JDUP;75.5;");
    }

    @Test
    @DisplayName("Returns 404 Not Found when campaign does not exist")
    void shouldReturn404_whenCampaignNotFound() throws Exception {
        // Given
        when(port.getProgressForDay(any(), any(), any(), any()))
                .thenThrow(new CampaignNotFoundException());

        // When / Then
        mockMvc.perform(get("/api/reporting/campaigns/unknown/interviewers/progress/export")
                        .param("date", "2025-06-10"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Returns 400 Bad Request when date is missing")
    void shouldReturnBadRequest_whenDateIsMissing() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress/export"))
                .andExpect(status().isBadRequest());
    }
}
