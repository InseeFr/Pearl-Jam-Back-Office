package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionByOrganizationUnitsPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByOrganizationUnitsResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByOrganizationUnitsPort;
import fr.insee.pearljam.domain.reporting.service.exception.FutureReportingDateException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignCollectionByOrganizationUnitControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingByOrganizationUnitsPort port;

    private static final CampaignCollectionByOrganizationUnitsResponse EMPTY_RESULT =
            new CampaignCollectionByOrganizationUnitsResponse(
                    List.of(),
                    new CampaignCollectionByOrganizationUnitsResponse.Campaign(
                            0L,
                            new CollectionRatesResponse(0f, 0f, 0f),
                            new ContactOutcomesProgressResponse(0L, 0L, 0L, 0L, 0L),
                            new ClosingCausesProgressResponse(0L, 0L, 0L)
                    )
            );

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByOrganizationUnitsPort.class);
        when(port.getProgressForDay(anyString(), anyString(), any(), any())).thenReturn(EMPTY_RESULT);

        CampaignCollectionByOrganizationUnitController controller =
                new CampaignCollectionByOrganizationUnitController(port, new CampaignCollectionByOrganizationUnitsPresenter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturnOk_whenDayProvided() throws Exception {
        LocalDate day = LocalDate.of(2025, 6, 10);

        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/collection")
                        .param("day", day.toString()))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(port).getProgressForDay(any(), eq("campaign-1"), dayCaptor.capture(), any());
        assertThat(dayCaptor.getValue()).isEqualTo(day);
    }

    @Test
    void shouldPassNullDay_whenDayIsNotProvided() throws Exception {
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/collection"))
                .andExpect(status().isOk());

        ArgumentCaptor<LocalDate> dayCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(port).getProgressForDay(any(), eq("campaign-1"), dayCaptor.capture(), any());
        assertThat(dayCaptor.getValue()).isNull();
    }

    @Test
    void shouldReturn404_whenCampaignNotFound() throws Exception {
        when(port.getProgressForDay(any(), anyString(), any(), any())).thenThrow(new CampaignNotFoundException());

        mockMvc.perform(get("/api/reporting/campaigns/unknown/organization-units/collection"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_whenDayIsInTheFuture() throws Exception {
        LocalDate futureDay = LocalDate.now().plusDays(1);
        when(port.getProgressForDay(any(), eq("campaign-1"), eq(futureDay), any()))
                .thenThrow(new FutureReportingDateException());

        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/organization-units/collection")
                        .param("day", futureDay.toString()))
                .andExpect(status().isBadRequest());
    }
}
