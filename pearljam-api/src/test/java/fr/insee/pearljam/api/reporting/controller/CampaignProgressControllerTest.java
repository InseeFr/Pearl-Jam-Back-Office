package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.CampaignProgressPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
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

class CampaignProgressControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingPort reportingService;

    @BeforeEach
    void setup() {
        reportingService = mock(CampaignReportingPort.class);
        
        CampaignDailyStats stats = new CampaignDailyStats();
        stats.setCampaignId("camp-1");
        stats.setCampaignLabel("Campaign 1");
        stats.setUpdatedAt(123456789L);
        
        CampaignProgressResponse response = new CampaignProgressResponse(
                "camp-1",
                "Campaign 1",
                0f,
                new StatesProgressResponse(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                new CommunicationsProgressResponse(0L, 0L),
                123456789L
        );
        
        when(reportingService.getCampaignsStats(any(), any(), any())).thenReturn(List.of(response));

        CampaignProgressController controller = new CampaignProgressController(
                reportingService,
                new CampaignProgressPresenter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Returns 200 OK with updatedAt field when day is provided")
    void shouldReturnOk_whenDayProvided() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/campaigns/progress")
                        .param("day", "2025-06-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].updatedAt").value(123456789L));
    }

    @Test
    @DisplayName("Returns 200 OK with updatedAt field when day is not provided")
    void shouldReturnOk_whenDayIsNotProvided() throws Exception {
        // Given / When / Then
        mockMvc.perform(get("/api/reporting/campaigns/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].updatedAt").value(123456789L));
    }

    @Test
    @DisplayName("Returns 400 Bad Request when day is in the future")
    void shouldReturnBadRequest_whenDayIsInTheFuture() throws Exception {
        // Given
        LocalDate futureDay = LocalDate.now().plusDays(1);
        when(reportingService.getCampaignsStats(any(), eq(futureDay), any()))
                .thenThrow(new FutureReportingDateException());

        // When / Then
        mockMvc.perform(get("/api/reporting/campaigns/progress")
                        .param("day", futureDay.toString()))
                .andExpect(status().isBadRequest());
    }
}
