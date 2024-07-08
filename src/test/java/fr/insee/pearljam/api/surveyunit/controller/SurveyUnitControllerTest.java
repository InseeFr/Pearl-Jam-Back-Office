package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.controller.SurveyUnitController;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.surveyunit.controller.dummy.SurveyUnitFakeService;
import fr.insee.pearljam.api.surveyunit.dto.CommentDto;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.dummy.AuthenticationUserFakeService;
import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.exception.PersonNotFoundException;
import fr.insee.pearljam.domain.exception.SurveyUnitNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SurveyUnitControllerTest {
    private MockMvc mockMvc;
    private SurveyUnitFakeService surveyUnitService;
    private String surveyUnitJson;
    private String comments;
    private String identification;
    private String surveyUnitTemplate;
    private final String updatePath = "/api/survey-unit/1";

    @BeforeEach
    void setup() {
        comments = """
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
        mockMvc.perform(put("/api/survey-unit/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitJson))
                .andDo(print())
                .andExpect(status().isOk());

        SurveyUnitDetailDto surveyUnitDetailDto = surveyUnitService.getSurveyUnitUpdated();
        assertThat(surveyUnitDetailDto.getId()).isEqualTo("su-id");
/*
        IdentificationDto identificationExpected = new IdentificationDto();
        identificationExpected.setIdentification(IdentificationQuestionValue.IDENTIFIED);
        identificationExpected.setAccess(AccessQuestionValue.ACC);
        identificationExpected.setCategory(CategoryQuestionValue.OCCASIONAL);
        identificationExpected.setOccupant(OccupantQuestionValue.IDENTIFIED);
        identificationExpected.setSituation(SituationQuestionValue.ABSORBED);

        assertThat(surveyUnitDetailDto.getIdentification()).isEqualTo(identificationExpected);
*/
        CommentDto commentExpected1 = new CommentDto(CommentType.MANAGEMENT, "5");
        CommentDto commentExpected2 = new CommentDto(CommentType.INTERVIEWER, "value");
        assertThat(surveyUnitDetailDto.getComments())
                .hasSize(2)
                .containsExactlyInAnyOrder(commentExpected1, commentExpected2);
    }

    @Test
    @DisplayName("Should return 404 when survey unit not exist")
    void updateSurveyUnit02() throws Exception {
        surveyUnitService.setShouldThrowSurveyUnitException(true);
        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(surveyUnitJson))
                .andExpect(MockMvcTestUtils.apiErrorMatches(HttpStatus.NOT_FOUND, updatePath, SurveyUnitNotFoundException.MESSAGE));
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

    @Test
    @DisplayName("Should return bad request when comment are invalid")
    void updateSurveyUnit04() throws Exception {
        List<String> invalidComments = List.of(
                """
                "comments": [
                    {
                        "type": "MANAGEMENT"
                    }
                ]""",
                """
                "comments": [
                    {
                        "value": "value"
                    }
                ]"""
        );

        for(String invalidComment : invalidComments) {
            surveyUnitJson = String.format(surveyUnitTemplate, invalidComment, identification);
            System.out.println(surveyUnitJson);
            mockMvc.perform(put(updatePath)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(surveyUnitJson))
                    .andDo(print())
                    .andExpect(
                            MockMvcTestUtils.apiErrorMatches(HttpStatus.BAD_REQUEST, updatePath, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE)
                    );
            assertThat(surveyUnitService.getSurveyUnitUpdated()).isNull();
        }
    }
}
