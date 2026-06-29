package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.export.SurveyUnitAssignedCsvExporter;
import fr.insee.pearljam.contracts.constants.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
public class SurveyUnitAssignedExportController {

    private final SurveyUnitAssignedCsvExporter surveyUnitAssignedCsvExporter;

    @Operation(summary = "Export assigned survey units as CSV file")
    @GetMapping(Constants.API_SURVEY_UNITS_ASSIGNED_EXPORT)
    @Parameter(name = "userId", hidden = true)
    public ResponseEntity<byte[]> exportSurveyUnitAssignedAsCsv(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String campaignId,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {
        log.info("Exporting survey units to review for user {} with search: {}", userId, search);
        return surveyUnitAssignedCsvExporter.export(userId, campaignId, search);
    }

}
