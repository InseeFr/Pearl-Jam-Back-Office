package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.controller.presenter.SurveyUnitToReviewApiPresenter;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToReviewPageResponse;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToReviewPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SurveyUnitToReviewControllerTest {

    private MockMvc mockMvc;
    private SurveyUnitToReviewPort port;

    private static final SurveyUnitToReviewPageResponse EMPTY_RESULT =
            new SurveyUnitToReviewPageResponse(
                    List.of(),
                    0,
                    20,
                    0L,
                    0
            );

    @BeforeEach
    void setup() {
        port = mock(SurveyUnitToReviewPort.class);
        when(port.getSurveyUnitsToReview(anyString(), anyString(), anyString(), anyBoolean(), any(Pageable.class), any(SurveyUnitToReviewApiPresenter.class)))
                .thenReturn(EMPTY_RESULT);

        SurveyUnitToReviewController controller =
                new SurveyUnitToReviewController(port, new SurveyUnitToReviewApiPresenter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void shouldReturnOk_whenSearchProvided() throws Exception {
        String searchTerm = "test-search";

        mockMvc.perform(get(Constants.API_SURVEY_UNITS_TO_REVIEW)
                        .param("search", searchTerm)
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "id,asc")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER)))
                .andExpect(status().isOk());

        verify(port).getSurveyUnitsToReview(nullable(String.class),nullable(String.class), eq(searchTerm), isNull(), any(Pageable.class), any(SurveyUnitToReviewApiPresenter.class));
    }

    @Test
    void shouldPassNullSearch_whenSearchIsNotProvided() throws Exception {
        mockMvc.perform(get(Constants.API_SURVEY_UNITS_TO_REVIEW)
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "id,asc")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER)))
                .andExpect(status().isOk());

        verify(port).getSurveyUnitsToReview(nullable(String.class),nullable(String.class), isNull(), isNull(), any(Pageable.class), any(SurveyUnitToReviewApiPresenter.class));
    }

    @Test
    void shouldPassCorrectPagination() throws Exception {
        mockMvc.perform(get(Constants.API_SURVEY_UNITS_TO_REVIEW)
                        .param("page", "2")
                        .param("size", "50")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER)))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(port).getSurveyUnitsToReview(
                nullable(String.class),
                nullable(String.class),
                any(),
                isNull(),
                pageableCaptor.capture(),
                any(SurveyUnitToReviewApiPresenter.class)
        );

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(2, pageable.getPageNumber());
        assertEquals(50, pageable.getPageSize());
    }

    @Test
    void shouldCallPortWithNullViewed_whenNotProvided() throws Exception {

        mockMvc.perform(get(Constants.API_SURVEY_UNITS_TO_REVIEW)
                        .param("search", "test-search")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER)))
                .andExpect(status().isOk());

        verify(port).getSurveyUnitsToReview(
                any(),
                any(),
                eq("test-search"),
                isNull(),
                any(Pageable.class),
                any()
        );
    }
}