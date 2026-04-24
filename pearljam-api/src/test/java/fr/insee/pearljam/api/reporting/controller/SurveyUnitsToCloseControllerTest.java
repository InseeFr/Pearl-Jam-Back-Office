package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitToClosePresenter;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToCloseResponse;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitsToClosePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SurveyUnitsToCloseControllerTest {

    private MockMvc mockMvc;
    private SurveyUnitsToClosePort surveyUnitsToClosePort;



    @BeforeEach
    void setUp() {
        surveyUnitsToClosePort = mock(SurveyUnitsToClosePort.class);
        SurveyUnitsToCloseController controller = new SurveyUnitsToCloseController(surveyUnitsToClosePort, new SurveyUnitToClosePresenter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();

    }

    @Test
    @DisplayName("Should return 200 and empty list when no survey units to close")
    void shouldReturnEmptyListWhenNoSurveyUnitsToClose() throws Exception {
        // Given
        when(surveyUnitsToClosePort.getSurveyUnitsToClose(any(), any()))
                .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get(Constants.API_REPORTING_SURVEYUNITS_TO_CLOSE))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("Should return 200 and list of survey units to close")
    void shouldReturnListOfSurveyUnitsToClose() throws Exception {
        // Given
        SurveyUnitToCloseResponse response1 = new SurveyUnitToCloseResponse(
                "Campaign 1", "SU-1", "Survey Unit 1", "Interviewer One",
                1, "ANV", null, "UNAVAILABLE", null
        );

        SurveyUnitToCloseResponse response2 = new SurveyUnitToCloseResponse(
                "Campaign 2", "SU-2", "Survey Unit 2", "Interviewer Two",
                2, "NVM", null, "COMPLETED", null
        );

        when(surveyUnitsToClosePort.getSurveyUnitsToClose(any(), any()))
                .thenReturn(List.of(response1, response2));

        // When & Then
        mockMvc.perform(get(Constants.API_REPORTING_SURVEYUNITS_TO_CLOSE))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                            {
                                "campaignLabel": "Campaign 1",
                                "surveyUnitId": "SU-1",
                                "surveyUnitDisplayName": "Survey Unit 1",
                                "interviewerLabel": "Interviewer One",
                                "ssech": 1,
                                "identificationState": "ANV",
                                "contactOutcome": null,
                                "questionnaireState": "UNAVAILABLE",
                                "closingCause": null
                            },
                            {
                                "campaignLabel": "Campaign 2",
                                "surveyUnitId": "SU-2",
                                "surveyUnitDisplayName": "Survey Unit 2",
                                "interviewerLabel": "Interviewer Two",
                                "ssech": 2,
                                "identificationState": "NVM",
                                "contactOutcome": null,
                                "questionnaireState": "COMPLETED",
                                "closingCause": null
                            }
                        ]
                        """));
    }


    @Test
    @DisplayName("Should handle internal server error gracefully")
    void shouldHandleInternalServerError() throws Exception {
        // Given
        when(surveyUnitsToClosePort.getSurveyUnitsToClose(any(), any()))
                .thenThrow(new RuntimeException("Internal server error"));

        // When & Then
        mockMvc.perform(get(Constants.API_REPORTING_SURVEYUNITS_TO_CLOSE))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> MockMvcTestUtils.apiErrorMatches(
                        org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                        Constants.API_REPORTING_SURVEYUNITS_TO_CLOSE,
                        "An error has occurred"
                ));
    }
}