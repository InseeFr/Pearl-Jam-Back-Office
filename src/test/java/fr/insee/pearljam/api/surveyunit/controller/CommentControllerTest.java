package fr.insee.pearljam.api.surveyunit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import fr.insee.pearljam.api.surveyunit.controller.dummy.CommentFakeService;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.api.utils.matcher.StructureDateMatcher;
import fr.insee.pearljam.api.web.exception.ApiExceptionComponent;
import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.exception.SurveyUnitNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest {

    private MockMvc mockMvc;
    private CommentFakeService commentService;
    private final String updatePath = "/api/survey-unit/1/comment";

    @BeforeEach
    void setup() {
        commentService = new CommentFakeService();
        ExceptionControllerAdvice exceptionControllerAdvice =
                new ExceptionControllerAdvice(new ApiExceptionComponent(new DefaultErrorAttributes()));
        CommentController commentController = new CommentController(commentService);
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new
                MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(new ObjectMapper().registerModule(new ParameterNamesModule()));
        mockMvc = MockMvcBuilders
                .standaloneSetup(commentController)
                .setControllerAdvice(exceptionControllerAdvice)
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .build();
    }

    @Test
    @DisplayName("Should update comment")
    void updateComment01() throws Exception {
        String comment = """
                {
                    "type": "INTERVIEWER",
                    "value": "5"
                }
                """;

        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comment))
                .andExpect(status().isOk());

        Comment commentUpdated = commentService.getCommentUpdated();
        assertThat(commentUpdated.type()).isEqualTo(CommentType.INTERVIEWER);
        assertThat(commentUpdated.value()).isEqualTo("5");
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"value\": \"5\"}", "{\"type\": \"INTERVIEWER\"}"})
    @DisplayName("Should return bad request when comment type or value is null")
    void updateComment02(String invalidComment) throws Exception {
        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidComment))
                .andExpect(MockMvcTestUtils
                        .apiErrorMatches(HttpStatus.BAD_REQUEST, updatePath, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE));
        assertThat(commentService.getCommentUpdated()).isNull();
    }

    @Test
    @DisplayName("Should return bad request when comment value is > 999")
    void updateComment03() throws Exception {
        String invalidValue = "\"" + new String(new char[1000]).replace('\0', 'a') + "\"";
        String invalidComment = """
                {
                    "type": "INTERVIEWER",
                    "value":""" + invalidValue + """
                }
                """;
        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidComment))
                .andExpect(MockMvcTestUtils
                        .apiErrorMatches(HttpStatus.BAD_REQUEST, updatePath, ExceptionControllerAdvice.INVALID_PARAMETERS_MESSAGE));
        assertThat(commentService.getCommentUpdated()).isNull();
    }

    @Test
    @DisplayName("Should return not found when survey unit does not exist")
    void updateComment04() throws Exception {
        commentService.setShouldThrowException(true);
        String comment = """
                {
                    "type": "INTERVIEWER",
                    "value": 5
                }
                """;

        mockMvc.perform(put(updatePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comment))
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()),
                        jsonPath("$.path").value(updatePath),
                        jsonPath("$.message").value(SurveyUnitNotFoundException.MESSAGE),
                        jsonPath("$.timestamp", new StructureDateMatcher()));
        assertThat(commentService.getCommentUpdated()).isNull();
    }
}
