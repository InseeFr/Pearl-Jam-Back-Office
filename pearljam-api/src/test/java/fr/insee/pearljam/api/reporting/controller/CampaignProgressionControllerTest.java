package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.reporting.service.CampaignProgressionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignProgressionControllerTest {

    private MockMvc mockMvc;
    private CampaignProgressionService progressionService;
    private static final LocalDate FIXED_TODAY = LocalDate.of(2025, 6, 15);
    private static final Clock FIXED_CLOCK = Clock.fixed(
            FIXED_TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

    @BeforeEach
    void setup() {
        progressionService = mock(CampaignProgressionService.class);
        when(progressionService.getCampaignsProgression(anyString(), any())).thenReturn(List.of());

        CampaignProgressionController controller =
                new CampaignProgressionController(progressionService, FIXED_CLOCK);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_whenDayProvided() throws Exception {
        LocalDate pastDate = FIXED_TODAY.minusDays(5);

        mockMvc.perform(get("/api/reporting/campaigns/progress")
                        .param("day", pastDate.toString()))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(progressionService).getCampaignsProgression(any(), dayCaptor.capture());
        assertThat(dayCaptor.getValue()).isEqualTo(pastDate);
    }

    @Test
    void shouldDefaultToToday_whenDayIsNull() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/progress"))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(progressionService).getCampaignsProgression(any(), dayCaptor.capture());
        assertThat(dayCaptor.getValue()).isEqualTo(FIXED_TODAY);
    }

    @Test
    void shouldDefaultToToday_whenDayIsInTheFuture() throws Exception {
        LocalDate futureDate = FIXED_TODAY.plusDays(10);

        mockMvc.perform(get("/api/reporting/campaigns/progress")
                        .param("day", futureDate.toString()))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(progressionService).getCampaignsProgression(any(), dayCaptor.capture());
        assertThat(dayCaptor.getValue()).isEqualTo(FIXED_TODAY);
    }
}
