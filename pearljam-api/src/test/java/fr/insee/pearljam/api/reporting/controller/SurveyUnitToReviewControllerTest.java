package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.SurveyUnitToReviewPresenter;
import fr.insee.pearljam.api.reporting.response.SurveyUnitToReviewResponse;
import fr.insee.pearljam.api.utils.AuthenticatedUserTestHelper;
import fr.insee.pearljam.api.utils.MockMvcTestUtils;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.reporting.port.in.SurveyUnitToReviewPort;
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

    private static final SurveyUnitToReviewResponse EMPTY_RESULT =
            new SurveyUnitToReviewResponse(
                    List.of(),
                    0,
                    20,
                    0L,
                    0
            );

    @BeforeEach
    void setup() {
        port = mock(SurveyUnitToReviewPort.class);
        when(port.getSurveyUnitsToReview(anyString(), anyString(), any(Pageable.class), any(SurveyUnitToReviewPresenter.class)))
                .thenReturn(EMPTY_RESULT);

        SurveyUnitToReviewController controller =
                new SurveyUnitToReviewController(port, new SurveyUnitToReviewPresenter());
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(MockMvcTestUtils.createExceptionControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void shouldReturnOk_whenSearchProvided() throws Exception {
        String searchTerm = "test-search";

        mockMvc.perform(get(Constants.API_REPORTING_SURVEY_UNITS_TO_REVIEW)
                        .param("search", searchTerm)
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "id,asc")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER)))
                .andExpect(status().isOk());

        verify(port).getSurveyUnitsToReview(nullable(String.class), eq(searchTerm), any(Pageable.class), any(SurveyUnitToReviewPresenter.class));
    }

    @Test
    void shouldPassNullSearch_whenSearchIsNotProvided() throws Exception {
        mockMvc.perform(get(Constants.API_REPORTING_SURVEY_UNITS_TO_REVIEW)
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "id,asc")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER)))
                .andExpect(status().isOk());

        verify(port).getSurveyUnitsToReview(nullable(String.class), isNull(), any(Pageable.class), any(SurveyUnitToReviewPresenter.class));
    }

    @Test
    void shouldPassCorrectPagination() throws Exception {
        mockMvc.perform(get(Constants.API_REPORTING_SURVEY_UNITS_TO_REVIEW)
                        .param("page", "2")
                        .param("size", "50")
                        .with(authentication(AuthenticatedUserTestHelper.AUTH_INTERVIEWER)))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(port).getSurveyUnitsToReview(
                nullable(String.class),
                any(),
                pageableCaptor.capture(),
                any(SurveyUnitToReviewPresenter.class)
        );

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(2, pageable.getPageNumber());
        assertEquals(50, pageable.getPageSize());
    }
}