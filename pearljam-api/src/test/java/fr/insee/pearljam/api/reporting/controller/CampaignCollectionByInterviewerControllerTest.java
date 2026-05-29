package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionByInterviewersPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByInterviewersResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
import fr.insee.pearljam.domain.reporting.service.exception.FutureReportingDateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CampaignCollectionByInterviewerControllerTest {

    private MockMvc mockMvc;
    private CampaignReportingByInterviewersPort port;

    private static final CampaignCollectionByInterviewersResponse EMPTY_RESULT =
            new CampaignCollectionByInterviewersResponse(
                    List.of(),
                    new CampaignCollectionByInterviewersResponse.OrganizationUnit(
                            0L,
                            new CollectionRatesResponse(0f, 0f, 0f),
                            new ContactOutcomesProgressResponse(0L, 0L, 0L, 0L, 0L),
                            new ClosingCausesProgressResponse(0L, 0L, 0L)
                    ),
                    new CampaignCollectionByInterviewersResponse.Campaign(
                            0L,
                            0L,
                            new CollectionRatesResponse(0f, 0f, 0f),
                            new ContactOutcomesProgressResponse(0L, 0L, 0L, 0L, 0L),
                            new ClosingCausesProgressResponse(0L, 0L, 0L)
                    )
            );

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByInterviewersPort.class);
        when(port.getProgressForDay(anyString(), anyString(), any(), any())).thenReturn(EMPTY_RESULT);

        CampaignCollectionByInterviewerController controller =
                new CampaignCollectionByInterviewerController(port, new CampaignCollectionByInterviewersPresenter());
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
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection")
                        .param("day", day.toString()))
                .andExpect(status().isOk());

        // Then
        verify(port).getProgressForDay(any(), eq("campaign-1"), eq(day), any());
    }

    @Test
    @DisplayName("Passes a null day to the port when day is not provided")
    void shouldPassNullDay_whenDayIsNotProvided() throws Exception {
        // Given / When
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection"))
                .andExpect(status().isOk());

        // Then
        verify(port).getProgressForDay(any(), eq("campaign-1"), isNull(), any());
    }

    @Test
    @DisplayName("Returns 404 Not Found when campaign does not exist")
    void shouldReturn404_whenCampaignNotFound() throws Exception {
        // Given
        when(port.getProgressForDay(any(), anyString(), any(), any())).thenThrow(new CampaignNotFoundException());

        // When / Then
        mockMvc.perform(get("/api/reporting/campaigns/unknown/interviewers/collection"))
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
        mockMvc.perform(get("/api/reporting/campaigns/campaign-1/interviewers/collection")
                        .param("day", futureDay.toString()))
                .andExpect(status().isBadRequest());
    }
}
