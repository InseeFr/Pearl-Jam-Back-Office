package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.InterviewerCampaignsProgressPresenter;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesInterviewerProgressResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsReportingPort;
import fr.insee.pearljam.domain.reporting.service.exception.FutureReportingDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InterviewerCampaignsProgressControllerTest {

    private MockMvc mockMvc;
    private InterviewerCampaignsReportingPort reportingService;

    @BeforeEach
    void setup() {
        reportingService = mock(InterviewerCampaignsReportingPort.class);
        
        InterviewerCampaignsProgressResponse response = new InterviewerCampaignsProgressResponse(
                "camp-1",
                "Campaign 1",
                0f,
                new StatesInterviewerProgressResponse(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new CommunicationsProgressResponse(0L, 0L),
                123456789L
        );
        
        when(reportingService.getCampaignsStatsForInterviewer(any(), any(), any(), any()))
                .thenReturn(List.of(response));

        InterviewerCampaignsProgressController controller = new InterviewerCampaignsProgressController(
                reportingService,
                new InterviewerCampaignsProgressPresenter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Returns 200 OK with updatedAt field when interviewerId and day are provided")
    void shouldReturnOk_whenInterviewerIdAndDayProvided() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/interviewers/{interviewerId}/campaigns/progress", "interviewer1")
                        .param("day", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].updatedAt").value(123456789L));
    }

    @Test
    @DisplayName("Returns 200 OK with updatedAt field when interviewerId is provided without day")
    void shouldReturnOk_whenInterviewerIdAndDayIsNotProvided() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/interviewers/{interviewerId}/campaigns/progress", "interviewer1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].updatedAt").value(123456789L));
    }

    @Test
    @DisplayName("Returns 400 Bad Request when day is in the future")
    void shouldReturnBadRequest_whenInterviewerIdAndDayIsInTheFuture() throws Exception {
        // Given
        LocalDate futureDay = LocalDate.now().plusDays(1);
        when(reportingService.getCampaignsStatsForInterviewer(any(), eq(futureDay), any(), any()))
                .thenThrow(new FutureReportingDateException());

        // When / Then
        mockMvc.perform(get("/api/reporting/interviewers/{interviewerId}/campaigns/progress", "interviewer1")
                        .param("day", futureDay.toString()))
                .andExpect(status().isBadRequest());
    }
}
