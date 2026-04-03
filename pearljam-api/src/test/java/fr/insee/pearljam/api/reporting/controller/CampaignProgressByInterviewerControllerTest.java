package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.CampaignProgressByInterviewersPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressByInterviewersResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CommunicationsProgress;
import fr.insee.pearljam.domain.reporting.readmodel.progress.StatesProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignProgressByInterviewerControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingByInterviewersPort port;

    private static final CampaignProgressByInterviewersResponse EMPTY_RESULT = new CampaignProgressByInterviewersResponse(
            List.of(),
            new CampaignProgressByInterviewersResponse.OrganizationUnit(0f,
                    new StatesProgress(
                            0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                    new CommunicationsProgress(0L, 0L)),
            new CampaignProgressByInterviewersResponse.Campaign(0L, 0f,
                    new StatesProgress(
                            0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                    new CommunicationsProgress(0L, 0L))
    );

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByInterviewersPort.class);
        when(port.getProgressForDay(anyString(), anyString(), any(), any())).thenReturn(EMPTY_RESULT);

        CampaignProgressByInterviewerController controller =
                new CampaignProgressByInterviewerController(port, new CampaignProgressByInterviewersPresenter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_whenDayProvided() throws Exception {
        LocalDate day = LocalDate.of(2025, 6, 10);

        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress")
                        .param("day", day.toString()))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(port).getProgressForDay(any(), eq("campaign-1"), dayCaptor.capture(), any());
        assertThat(dayCaptor.getValue()).isEqualTo(day);
    }

    @Test
    void shouldPassNullDay_whenDayIsNotProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/progress"))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(port).getProgressForDay(any(), eq("campaign-1"), dayCaptor.capture(), any());
        assertThat(dayCaptor.getValue()).isNull();
    }

    @Test
    void shouldReturn404_whenCampaignNotFound() throws Exception {
        when(port.getProgressForDay(any(), anyString(), any(), any())).thenThrow(new CampaignNotFoundException());

        mockMvc.perform(get("/api/reporting/campaigns/unknown/interviewers/progress"))
                .andExpect(status().isNotFound());
    }
}
