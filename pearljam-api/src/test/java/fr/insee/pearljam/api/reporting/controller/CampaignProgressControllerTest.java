package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.InterviewerCampaignsProgressPresenter;
import fr.insee.pearljam.api.reporting.presenter.CampaignProgressPresenter;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import fr.insee.pearljam.domain.reporting.service.exception.FutureReportingDateException;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignProgressControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingPort reportingService;

    @BeforeEach
    void setup() {
        reportingService = mock(CampaignReportingPort.class);
        when(reportingService.getCampaignsStats(any(), any(), any())).thenReturn(List.of());
        when(reportingService.getCampaignsStatsForInterviewer(any(), any(), any(), any())).thenReturn(List.of());

        CampaignProgressController controller = new CampaignProgressController(
                reportingService,
                new CampaignProgressPresenter(),
                new InterviewerCampaignsProgressPresenter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_whenDayProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/progress")
                        .param("day", "2025-06-10"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOk_whenDayIsNotProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/progress"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequest_whenDayIsInTheFuture() throws Exception {
        LocalDate futureDay = LocalDate.now().plusDays(1);
        when(reportingService.getCampaignsStats(any(), eq(futureDay), any()))
                .thenThrow(new FutureReportingDateException());

        mockMvc.perform(get("/api/reporting/campaigns/progress")
                        .param("day", futureDay.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOk_whenInterviewerIdAndDayProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/interviewers/{interviewerId}/campaigns/progress", "interviewer1")
                        .param("day", "2025-06-10"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOk_whenInterviewerIdAndDayIsNotProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/interviewers/{interviewerId}/campaigns/progress", "interviewer1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequest_whenInterviewerIdAndDayIsInTheFuture() throws Exception {
        LocalDate futureDay = LocalDate.now().plusDays(1);
        when(reportingService.getCampaignsStatsForInterviewer(any(), eq(futureDay), any(), any()))
                .thenThrow(new FutureReportingDateException());

        mockMvc.perform(get("/api/reporting/interviewers/{interviewerId}/campaigns/progress", "interviewer1")
                        .param("day", futureDay.toString()))
                .andExpect(status().isBadRequest());
    }
}
