package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.controller.request.CloseSurveyUnitsRequest;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static fr.insee.pearljam.contracts.constants.Constants.API_SURVEYUNIT_CLOSE_SURVEYUNITS;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
public class SurveyUnitClosingController {

    private final SurveyUnitClosingPort surveyUnitClosingPort;

    /**
     * Add closing cause to multiple survey units
     */
    @PostMapping(API_SURVEYUNIT_CLOSE_SURVEYUNITS)
    public ResponseEntity<Void> addClosingCauseToMultipleSurveyUnits(
            @PathVariable("closingCause") ClosingCauseType closingCause,
            @RequestBody @Valid CloseSurveyUnitsRequest request) {

        surveyUnitClosingPort.addClosingCauseToMultipleSurveyUnits(request.getSurveyUnitIds(), closingCause);
        return ResponseEntity.noContent().build();
    }
}
