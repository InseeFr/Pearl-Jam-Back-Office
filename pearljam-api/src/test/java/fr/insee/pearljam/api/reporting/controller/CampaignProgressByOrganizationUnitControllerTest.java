package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.CampaignProgressByOrganizationUnitsPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressByOrganizationUnitsResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByOrganizationUnitsPort;
import fr.insee.pearljam.domain.reporting.service.exception.FutureReportingDateException;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

class CampaignProgressByOrganizationUnitControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingByOrganizationUnitsPort port;

    private static final CampaignProgressByOrganizationUnitsResponse EMPTY_RESULT = new CampaignProgressByOrganizationUnitsResponse(
            List.of(),
            new CampaignProgressByOrganizationUnitsResponse.Campaign(0f,
                    new StatesProgressResponse(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L),
                    new CommunicationsProgressResponse(0L, 0L))
    );

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByOrganizationUnitsPort.class);
        when(port.getProgressForDay(anyString(), anyString(), any(), any())).thenReturn(EMPTY_RESULT);

        CampaignProgressByOrganizationUnitController controller =
                new CampaignProgressByOrganizationUnitController(port, new CampaignProgressByOrganizationUnitsPresenter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Returns 200 OK and forwards the day to the port when day is provided")
    void shouldReturnOk_whenDayProvided() throws Exception {
        // Given
        LocalDate day = LocalDate.of(2025, 6, 10);

        // When
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/progress")
                        .param("day", day.toString()))
                .andExpect(status().isOk());

        // Then
        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(port).getProgressForDay(any(), eq("campaign-1"), dayCaptor.capture(), any());
        assertThat(dayCaptor.getValue()).isEqualTo(day);
    }

    @Test
    @DisplayName("Passes a null day to the port when day is not provided")
    void shouldPassNullDay_whenDayIsNotProvided() throws Exception {
        // Given / When
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/progress"))
                .andExpect(status().isOk());

        // Then
        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(port).getProgressForDay(any(), eq("campaign-1"), dayCaptor.capture(), any());
        assertThat(dayCaptor.getValue()).isNull();
    }

    @Test
    @DisplayName("Returns 404 Not Found when campaign does not exist")
    void shouldReturn404_whenCampaignNotFound() throws Exception {
        // Given
        when(port.getProgressForDay(any(), anyString(), any(), any())).thenThrow(new CampaignNotFoundException());

        // When / Then
        mockMvc.perform(get("/api/reporting/campaigns/unknown/organization-units/progress"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Returns 400 Bad Request when day is in the future")
    void shouldReturnBadRequest_whenDayIsInTheFuture() throws Exception {
        // Given
        LocalDate futureDay = LocalDate.now().plusDays(1);
        when(port.getProgressForDay(any(), eq("campaign-1"), eq(futureDay), any()))
                .thenThrow(new FutureReportingDateException());

        // When / Then
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/progress")
                        .param("day", futureDay.toString()))
                .andExpect(status().isBadRequest());
    }
}
