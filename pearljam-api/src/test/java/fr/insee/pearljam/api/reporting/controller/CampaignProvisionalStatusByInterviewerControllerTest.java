package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.CampaignProvisionalStatusByInterviewerApiPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProvisionalStatusByInterviewersResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
import fr.insee.pearljam.domain.reporting.service.exception.FutureReportingDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignProvisionalStatusByInterviewerControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingByInterviewersPort reportingService;
    private static final LocalDate FIXED_TODAY = LocalDate.of(2025, 6, 5);

    private static final CampaignProvisionalStatusByInterviewersResponse EMPTY_RESULT =
            new CampaignProvisionalStatusByInterviewersResponse(
                    List.of(),
                    new CampaignProvisionalStatusByInterviewersResponse.OrganizationUnitSite(
                            new CampaignProvisionalStatusByInterviewersResponse.OrganizationUnitSite.SurveyUnitsSiteResponse(
                                    0L,
                                    new CampaignProvisionalStatusByInterviewersResponse
                                            .OrganizationUnitSite.SurveyUnitsSiteResponse
                                            .ClosingCauseSiteResponse(
                                            0L,
                                            0L,
                                            0L,
                                            0L,
                                            0L)
                            )
                    )
            );

    @BeforeEach
    void setup() {
        reportingService = mock(CampaignReportingByInterviewersPort.class);

        when(reportingService.getProgressForDay(any(), any(), any(), any()))
                .thenReturn(EMPTY_RESULT);

        CampaignProvisionalStatusByInterviewerController controller =
                new CampaignProvisionalStatusByInterviewerController(
                        reportingService,
                        new CampaignProvisionalStatusByInterviewerApiPresenter());

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Returns 200 OK when day is provided")
    void shouldReturnOk_whenDayProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/{campaignId}/interviewers/provisional-status", "campaign-1")
                        .param("day", "2025-06-10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns 200 OK when day is not provided")
    void shouldReturnOk_whenDayIsNotProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/{campaignId}/interviewers/provisional-status", "campaign-1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns 400 Bad Request when day is in the future")
    void shouldReturnBadRequest_whenDayIsInTheFuture() throws Exception {
        Clock fixedClock = Clock.fixed(
                FIXED_TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(),
                ZoneOffset.UTC);
        LocalDate futureDay = LocalDate.now(fixedClock).plusDays(1);

        when(reportingService.getProgressForDay(any(), eq("campaign-1"), eq(futureDay), any()))
                .thenThrow(new FutureReportingDateException());

        mockMvc.perform(get("/api/reporting/campaigns/{campaignId}/interviewers/provisional-status", "campaign-1")
                        .param("day", futureDay.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Returns 404 Not Found when campaign does not exist")
    void shouldReturnNotFound_whenCampaignDoesNotExist() throws Exception {
        when(reportingService.getProgressForDay(any(), eq("unknown-campaign"), any(), any()))
                .thenThrow(new CampaignNotFoundExceptionRuntime());

        mockMvc.perform(get("/api/reporting/campaigns/{campaignId}/interviewers/provisional-status", "unknown-campaign"))
                .andExpect(status().isNotFound());
    }
}