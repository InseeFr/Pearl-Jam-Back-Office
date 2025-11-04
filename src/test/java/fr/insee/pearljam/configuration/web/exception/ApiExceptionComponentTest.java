package fr.insee.pearljam.configuration.web.exception;


import fr.insee.pearljam.configuration.web.exception.ApiError;
import fr.insee.pearljam.configuration.web.exception.ApiExceptionComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionComponentTest {
    private ApiExceptionComponent exceptionComponent;
    private ErrorAttributes errorAttributes;

    @BeforeEach
    void setup() {
        errorAttributes = new DefaultErrorAttributes();
        exceptionComponent = new ApiExceptionComponent(errorAttributes);
    }

    @Test
    @DisplayName("Should return the api error object")
    void testBuildApiError01() {
        String uri = "/v1/someuri";
        String errorLabel = "error message";
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setServerName("www.example.com");
        servletRequest.setRequestURI(uri);

        ServletWebRequest servletWebRequest = new ServletWebRequest(servletRequest);

        ApiError error = exceptionComponent.buildApiErrorObject(servletWebRequest, HttpStatus.NOT_FOUND, errorLabel);

        Date maxDateExpected = new Date();

        assertThat(error.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(error.getPath()).isEqualTo(uri);
        assertThat(error.getMessage()).isEqualTo(errorLabel);
        assertThat(maxDateExpected).isCloseTo(error.getTimestamp(), 2000);
        assertThat(error.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
