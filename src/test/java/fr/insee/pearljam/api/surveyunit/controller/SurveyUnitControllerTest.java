package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.controller.SurveyUnitController;
import fr.insee.pearljam.api.surveyunit.controller.dummy.SurveyUnitFakeService;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.surveyunit.dto.IdentificationDto;
import fr.insee.pearljam.api.surveyunit.dto.SurveyUnitUpdateDto;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import fr.insee.pearljam.domain.exception.PersonNotFoundException;
import fr.insee.pearljam.domain.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SurveyUnitControllerTest {
    private MockMvc mockMvc;
    private SurveyUnitFakeService surveyUnitService;
    private String surveyUnitJson;
    private String identification;
    private String surveyUnitTemplate;
    private final String updatePath = "/api/survey-unit/1";

    @BeforeEach
    void setup() {
        String comments = """
                "comments": [
                    {
                        "type": "MANAGEMENT",
                            "value": "5"
                    },
                    {
                        "type": "INTERVIEWER",
                            "value": "value"
                    }
                ]""";
        identification = """
                "identification": {
                        "identification": "IDENTIFIED",
                        "access": "ACC",
                        "situation": "ABSORBED",
                        "category": "OCCASIONAL",
                        "occupant": "IDENTIFIED"
                    }
                """;
        surveyUnitTemplate = """
                {
                    "id": "su-id",
                    %s,
                    %s
                }
                """;
        surveyUnitJson = String.format(surveyUnitTemplate, comments, identification);
        surveyUnitService = new SurveyUnitFakeService();
        ExceptionControllerAdvice exceptionControllerAdvice = MockMvcTestUtils.createExceptionControllerAdvice();
        Authentication authUser = AuthenticatedUserTestHelper.AUTH_ADMIN;
        AuthenticationUserFakeService authService = new AuthenticationUserFakeService(authUser);
        SurveyUnitController surveyUnitController = new SurveyUnitController(surveyUnitService, authService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(surveyUnitController)
                .setControllerAdvice(exceptionControllerAdvice)
                .build();
    }

    @Test
    @DisplayName("Should update survey unit")
    void updateSurveyUnit01() throws Exception {
        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitJson))
                .andExpect(status().isOk());

        SurveyUnitUpdateDto surveyUnitUpdated = surveyUnitService.getSurveyUnitUpdated();
        assertThat(surveyUnitUpdated.id()).isEqualTo("su-id");

        IdentificationDto identificationExpected = new IdentificationDto(IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ABSORBED,
                CategoryQuestionValue.OCCASIONAL,
                OccupantQuestionValue.IDENTIFIED
                , null, null, null, null, null
        );

        assertThat(surveyUnitUpdated.identification()).isEqualTo(identificationExpected);

        CommentDto commentExpected1 = new CommentDto(CommentType.MANAGEMENT, "5");
        CommentDto commentExpected2 = new CommentDto(CommentType.INTERVIEWER, "value");
        assertThat(surveyUnitUpdated.comments())
                .hasSize(2)
                .containsExactlyInAnyOrder(commentExpected1, commentExpected2);
        assertThat(surveyUnitUpdated.identification()).isEqualTo(identificationExpected);
    }

    @Test
    @DisplayName("Should return 404 when survey unit not exist")
    void updateSurveyUnit02() throws Exception {
        surveyUnitService.setShouldThrowSurveyUnitException(true);
        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitJson))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, updatePath, String.format(SurveyUnitNotFoundException.MESSAGE,1)));
        assertThat(surveyUnitService.getSurveyUnitUpdated()).isNull();
    }

    @Test
    @DisplayName("Should return 404 when person not exist")
    void updateSurveyUnit03() throws Exception {
        surveyUnitService.setShouldThrowPersonException(true);
        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitJson))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, updatePath, PersonNotFoundException.MESSAGE));
        assertThat(surveyUnitService.getSurveyUnitUpdated()).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"comments\": [{\"value\": \"5\"}]",
            "\"comments\": [{\"type\": \"INTERVIEWER\"}]"
    })
    @DisplayName("Should return bad request when comment are invalid")
    void updateSurveyUnit04(String invalidComment) throws Exception {
         surveyUnitJson = String.format(surveyUnitTemplate, invalidComment, identification);
        System.out.println(surveyUnitJson);
        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitJson))
                .andExpect(
                        MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, updatePath, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE)
                );
        assertThat(surveyUnitService.getSurveyUnitUpdated()).isNull();
    }
}
