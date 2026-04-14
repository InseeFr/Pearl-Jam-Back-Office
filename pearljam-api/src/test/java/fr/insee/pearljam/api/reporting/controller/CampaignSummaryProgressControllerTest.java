package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.reporting.service.CampaignSummaryProgressService;
import fr.insee.pearljam.domain.reporting.service.exception.FutureReportingDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignSummaryProgressControllerTest {

    private MockMvc mockMvc;
    private CampaignSummaryProgressService summaryService;

    @BeforeEach
    void setup() {
        summaryService = mock(CampaignSummaryProgressService.class);
        when(summaryService.getCampaignSummaryProgress(any(), any())).thenReturn(List.of());

        CampaignSummaryProgressController controller =
                new CampaignSummaryProgressController(summaryService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_whenDayProvided() throws Exception {
        LocalDate day = LocalDate.of(2025, 6, 10);

        mockMvc.perform(get("/api/reporting/campaigns/summary")
                        .param("day", day.toString()))
                .andExpect(status().isOk());

        verify(summaryService).getCampaignSummaryProgress(any(), eq(day));
    }

    @Test
    void shouldPassNullDay_whenDayIsNotProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/summary"))
                .andExpect(status().isOk());

        verify(summaryService).getCampaignSummaryProgress(any(), isNull());
    }

    @Test
    void shouldReturnBadRequest_whenDayIsInTheFuture() throws Exception {
        LocalDate futureDay = LocalDate.now().plusDays(1);
        when(summaryService.getCampaignSummaryProgress(any(), eq(futureDay)))
                .thenThrow(new FutureReportingDateException());

        mockMvc.perform(get("/api/reporting/campaigns/summary")
                        .param("day", futureDay.toString()))
                .andExpect(status().isBadRequest());
    }
}
