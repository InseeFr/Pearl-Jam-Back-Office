package fr.insee.pearljam.api.utils;

import fr.insee.pearljam.api.utils.matcher.StructureDateMatcher;
import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MockMvcTestUtils {

    public static ResultMatcher apiErrorMatches(HttpStatus errorStatus, String path, String message) {
        return result -> {
            status().is(errorStatus.value()).match(result);
            jsonPath("$.code").value(errorStatus.value()).match(result);
            jsonPath("$.path").value(path).match(result);
            jsonPath("$.message").value(message).match(result);
            jsonPath("$.timestamp", new StructureDateMatcher()).match(result);
        };
    }

    public static ExceptionControllerAdvice createExceptionControllerAdvice() {
        return new ExceptionControllerAdvice(new DefaultErrorAttributes());
    }

    public static LocalDate getDate() {
        Instant fixedInstant = Instant.ofEpochMilli(1719324512000L);
        return LocalDate.ofInstant(fixedInstant, ZoneId.systemDefault());
    }
}