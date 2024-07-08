package fr.insee.pearljam.api.utils;

import fr.insee.pearljam.api.utils.matcher.StructureDateMatcher;
import fr.insee.pearljam.api.web.exception.ApiExceptionComponent;
import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MockMvcTestUtils {

    public static ResultMatcher apiErrorMatches(HttpStatus errorStatus, String path, String message) {
        return result -> {
            status().is(errorStatus.value());
            jsonPath("$.code").value(errorStatus.value());
            jsonPath("$.path").value(path);
            jsonPath("$.message").value(message);
            jsonPath("$.timestamp", new StructureDateMatcher());
        };
    }

    public static ExceptionControllerAdvice createExceptionControllerAdvice() {
        return new ExceptionControllerAdvice(new ApiExceptionComponent(new DefaultErrorAttributes()));
    }
}