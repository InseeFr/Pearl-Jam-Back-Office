package fr.insee.pearljam.api.utils;

import fr.insee.pearljam.api.web.exception.ExceptionControllerAdvice;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
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
            jsonPath("$.status").value(errorStatus.value()).match(result);
            jsonPath("$.instance").value(path).match(result);
            jsonPath("$.detail").value(message).match(result);
        };
    }

    public static ExceptionControllerAdvice createExceptionControllerAdvice() {
        return new ExceptionControllerAdvice();
    }

    public static LocalDate getDate() {
        Instant fixedInstant = Instant.ofEpochMilli(FixedDateService.FIXED_TIMESTAMP);
        return LocalDate.ofInstant(fixedInstant, ZoneId.systemDefault());
    }
}