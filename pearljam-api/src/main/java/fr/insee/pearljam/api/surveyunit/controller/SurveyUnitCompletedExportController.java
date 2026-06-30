package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.controller.export.completed.SurveyUnitCompletedCsvExporter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static fr.insee.pearljam.contracts.constants.Constants.API_CAMPAIGN_SU_COMPLETED_EXPORT;

@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
public class SurveyUnitCompletedExportController {

    private final SurveyUnitCompletedCsvExporter csvExporter;

    @GetMapping(API_CAMPAIGN_SU_COMPLETED_EXPORT)
    public ResponseEntity<byte[]> getCompletedSurveyUnitsForCampaign(
            @PathVariable @NotBlank String id)
    {

        log.info("Export survey units completed");
        return csvExporter.export(id);
    }
}