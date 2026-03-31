package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignProgressionByInterviewersPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignProgressionByInterviewers;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignProgressionByInterviewerControllerTest {

    private MockMvc mockMvc;
    private CampaignProgressionByInterviewersPort port;
    private static final LocalDate FIXED_TODAY = LocalDate.of(2025, 6, 15);
    private static final Clock FIXED_CLOCK = Clock.fixed(
            FIXED_TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

    private static final CampaignProgressionByInterviewers EMPTY_RESULT = new CampaignProgressionByInterviewers(
            List.of(),
            new CampaignProgressionByInterviewers.OrganizationUnit(0f,
                    new CampaignProgressionByInterviewers.CampaignProgressionByInterviewersSurveyUnits(
                            0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L)),
            new CampaignProgressionByInterviewers.Campaign(0f,
                    new CampaignProgressionByInterviewers.CampaignProgressionByInterviewersSurveyUnits(
                            0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L))
    );

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignProgressionByInterviewersPort.class);
        when(port.getProgressionForDay(anyString(), anyString(), any())).thenReturn(EMPTY_RESULT);

        CampaignProgressionByInterviewerController controller =
                new CampaignProgressionByInterviewerController(port, FIXED_CLOCK);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_whenDayProvided() throws Exception {
        LocalDate pastDate = FIXED_TODAY.minusDays(5);

        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress")
                        .param("day", pastDate.toString()))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(port).getProgressionForDay(any(), eq("campaign-1"), dayCaptor.capture());
        assertThat(dayCaptor.getValue()).isEqualTo(pastDate);
    }

    @Test
    void shouldDefaultToToday_whenDayIsNull() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress"))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(port).getProgressionForDay(any(), eq("campaign-1"), dayCaptor.capture());
        assertThat(dayCaptor.getValue()).isEqualTo(FIXED_TODAY);
    }

    @Test
    void shouldDefaultToToday_whenDayIsInTheFuture() throws Exception {
        LocalDate futureDate = FIXED_TODAY.plusDays(10);

        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress")
                        .param("day", futureDate.toString()))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(port).getProgressionForDay(any(), eq("campaign-1"), dayCaptor.capture());
        assertThat(dayCaptor.getValue()).isEqualTo(FIXED_TODAY);
    }

    @Test
    void shouldReturn404_whenCampaignNotFound() throws Exception {
        when(port.getProgressionForDay(any(), anyString(), any())).thenThrow(new CampaignNotFoundException());

        mockMvc.perform(get("/api/reporting/campaigns/unknown/interviewers/progress"))
                .andExpect(status().isNotFound());
    }
}
