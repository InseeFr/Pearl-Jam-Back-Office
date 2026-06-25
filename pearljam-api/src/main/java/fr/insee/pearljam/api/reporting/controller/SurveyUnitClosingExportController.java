package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.surveyunit.SurveyUnitClosingApiCsvPresenter;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToCloseResponse;
import fr.insee.pearljam.contracts.constants.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Validated
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
public class SurveyUnitClosingExportController {

    private final SurveyUnitClosingApiCsvPresenter presenter;

    @Operation(summary = "Get survey units to close for management UI")
    @GetMapping(Constants.API_SURVEYUNITS_TO_CLOSE)
    @Parameter(name = "userId", hidden = true)
    public List<SurveyUnitToCloseResponse> getSurveyUnitsToCloseExport(
            @CurrentSecurityContext(expression = "authentication.name") String userId) {
        log.info("Retrieving survey units to close for user {}", userId);



        return csvExporter.export(userId);

    }
}
