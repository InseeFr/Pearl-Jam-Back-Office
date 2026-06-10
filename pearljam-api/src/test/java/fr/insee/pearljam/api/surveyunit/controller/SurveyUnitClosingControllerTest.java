package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.controller.request.CloseSurveyUnitsRequest;
import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitClosingApiPresenter;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToCloseResponse;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import fr.insee.pearljam.domain.surveyunit.service.exception.ClosingCauseAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("SurveyUnitClosingController - Unit Tests")
class SurveyUnitClosingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SurveyUnitClosingPort surveyUnitClosingPort;

    @BeforeEach
    void setUp() {
        SurveyUnitClosingController controller =
                new SurveyUnitClosingController(
                        surveyUnitClosingPort,
                        new SurveyUnitClosingApiPresenter()
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .build();
    }

    @Test
    void shouldReturn204WhenClosingSurveyUnit() throws Exception {
        // given
        doNothing().when(surveyUnitClosingPort)
                .addClosingCauseToMultipleSurveyUnits(anyList(), any(), anyBoolean());

        CloseSurveyUnitsRequest request = new CloseSurveyUnitsRequest();
        request.setSurveyUnitIds(List.of("12"));
        request.setClosingCauseType(ClosingCauseType.NPA);

        // when / then
        mockMvc.perform(post(Constants.API_SURVEYUNIT_CLOSE_SURVEYUNITS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isNoContent());

        verify(surveyUnitClosingPort)
                .addClosingCauseToMultipleSurveyUnits(anyList(), any(), anyBoolean());
    }

    @Test
    void shouldReturn409WhenConflict() throws Exception {
        doThrow(new ClosingCauseAlreadyExistsException("11"))
                .when(surveyUnitClosingPort)
                .addClosingCauseToMultipleSurveyUnits(anyList(), any(), anyBoolean());


        CloseSurveyUnitsRequest request = new CloseSurveyUnitsRequest();
        request.setSurveyUnitIds(List.of("11"));
        request.setClosingCauseType(ClosingCauseType.NPA);

        mockMvc.perform(post(Constants.API_SURVEYUNIT_CLOSE_SURVEYUNITS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonMapper.shared().writeValueAsBytes(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnSurveyUnitsToClose() throws Exception {
        when(surveyUnitClosingPort.getSurveyUnitsToClose(any(), any()))
                .thenReturn(List.of(
                        new SurveyUnitToCloseResponse("C1","SU1","Name","Interviewer",1,"ANV",null, "UNAVAILABLE",null)
                ));

        mockMvc.perform(get(Constants.API_SURVEYUNITS_TO_CLOSE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].surveyUnitId").value("SU1"));
    }

    @Test
    void shouldHandleInternalError() throws Exception {
        when(surveyUnitClosingPort.getSurveyUnitsToClose(any(), any()))
                .thenThrow(new RuntimeException());

        mockMvc.perform(get(Constants.API_SURVEYUNITS_TO_CLOSE))
                .andExpect(status().isInternalServerError());
    }
}