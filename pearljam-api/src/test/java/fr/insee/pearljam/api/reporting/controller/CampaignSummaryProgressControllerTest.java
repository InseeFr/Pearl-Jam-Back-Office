package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.reporting.service.CampaignSummaryProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignSummaryProgressControllerTest {

    private MockMvc mockMvc;
    private CampaignSummaryProgressService summaryService;

    @BeforeEach
    void setup() {
        summaryService = mock(CampaignSummaryProgressService.class);
        when(summaryService.getCampaignSummaryProgress(anyString(), any())).thenReturn(List.of());

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

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(summaryService).getCampaignSummaryProgress(any(), dayCaptor.capture());
        assertThat(dayCaptor.getValue()).isEqualTo(day);
    }

    @Test
    void shouldPassNullDay_whenDayIsNotProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/summary"))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(summaryService).getCampaignSummaryProgress(any(), dayCaptor.capture());
        assertThat(dayCaptor.getValue()).isNull();
    }
}
