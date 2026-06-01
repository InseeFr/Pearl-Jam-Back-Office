package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.surveyunit.controller.SurveyUnitCompletedController;
import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitCompletedApiPresenter;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitCompletedResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.in.application.SurveyUnitCompletedPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SurveyUnitCompletedControllerTest {

    private MockMvc mockMvc;
    private SurveyUnitCompletedPort surveyUnitCompletedPort;

    @BeforeEach
    void setup() {
        surveyUnitCompletedPort = mock(SurveyUnitCompletedPort.class);
        SurveyUnitCompletedApiPresenter surveyUnitCompletedApiPresenter = mock(SurveyUnitCompletedApiPresenter.class);

        SurveyUnitCompletedController controller = new SurveyUnitCompletedController(
                surveyUnitCompletedPort,
                surveyUnitCompletedApiPresenter);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    @DisplayName("Returns 200 OK when campaign exists")
    void shouldReturnOk_whenCampaignExists() throws Exception {
        when(surveyUnitCompletedPort.getCompletedSurveyUnits(eq("campaign-01"), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/campaign/{id}/survey-units/completed", "campaign-01"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Returns 200 OK with empty list when no completed survey units found")
    void shouldReturnEmptyList_whenNoCompletedSurveyUnits() throws Exception {
        when(surveyUnitCompletedPort.getCompletedSurveyUnits(eq("campaign-01"), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/campaign/{id}/survey-units/completed", "campaign-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Returns 200 OK with survey units in response body")
    void shouldReturnSurveyUnits_whenCompletedSurveyUnitsExist() throws Exception {
        List<SurveyUnitCompletedResponse> responses = List.of(
                new SurveyUnitCompletedResponse(
                        "su-1",
                        "Household Survey 1",
                        "John Doe",
                        "2024-01-15",
                        ContactOutcomeType.INA,
                        ClosingCauseType.NPI,
                        false,
                        "https://example.com/review/interrogations/su-1",
                        "A comment"
                ),
                new SurveyUnitCompletedResponse(
                        "su-2",
                        "Household Survey 2",
                        "Jane Smith",
                        "2024-01-16",
                        null,
                        null,
                        true,
                        "https://example.com/review/interrogations/su-2",
                        null
                )
        );

        when(surveyUnitCompletedPort.getCompletedSurveyUnits(eq("campaign-01"), any()))
                .thenReturn(responses);

        mockMvc.perform(get("/api/campaign/{id}/survey-units/completed", "campaign-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].surveyUnitId").value("su-1"))
                .andExpect(jsonPath("$[0].surveyUnitDisplayName").value("Household Survey 1"))
                .andExpect(jsonPath("$[0].interviewerLabel").value("John Doe"))
                .andExpect(jsonPath("$[0].endDate").value("2024-01-15"))
                .andExpect(jsonPath("$[0].contactOutcome").value("INA"))
                .andExpect(jsonPath("$[0].closingCauseType").value("NPI"))
                .andExpect(jsonPath("$[0].read").value(false))
                .andExpect(jsonPath("$[0].readOnlyUrl").value("https://example.com/review/interrogations/su-1"))
                .andExpect(jsonPath("$[0].comment").value("A comment"))
                .andExpect(jsonPath("$[1].surveyUnitId").value("su-2"))
                .andExpect(jsonPath("$[1].contactOutcome").doesNotExist())
                .andExpect(jsonPath("$[1].closingCauseType").doesNotExist())
                .andExpect(jsonPath("$[1].read").value(true))
                .andExpect(jsonPath("$[1].comment").doesNotExist());
    }

    @Test
    @DisplayName("Returns 404 Bad Request when campaign does not exists")
    void shouldReturnBadRequest_whenCampaignIdIsBlank() throws Exception {

        when(surveyUnitCompletedPort.getCompletedSurveyUnits(eq("campaign-01"), any()))
                .thenThrow(CampaignNotFoundExceptionRuntime.class);

        mockMvc.perform(get("/api/campaign/{id}/survey-units/completed", "campaign-01"))
                .andExpect(status().isNotFound());
    }
}