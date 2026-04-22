package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.reporting.port.in.SurveyUnitsToClosePort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitToClose;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
@Validated
public class SurveyUnitsToCloseController {

    private final SurveyUnitsToClosePort surveyUnitsToClosePort;

    @Operation(summary = "Get survey units to close for reporting")
    @GetMapping(Constants.API_REPORTING_SURVEYUNITS_TO_CLOSE)
    @Parameter(name = "userId", hidden = true)
    public List<SurveyUnitToClose> getSurveyUnitsToClose(
            HttpServletRequest request,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {
        return surveyUnitsToClosePort.getSurveyUnitsToClose(userId, request);
    }
}
