package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.controller.request.SurveyUnitsNewStateRequest;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitStatePort;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static fr.insee.pearljam.contracts.constants.Constants.API_SURVEYUNITS_ADD_STATE;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
public class SurveyUnitStateController {

    private final SurveyUnitStatePort surveyUnitStatePort;

    /**
     * Add specific state to multiple survey units
     */
    @PostMapping(API_SURVEYUNITS_ADD_STATE)
    public ResponseEntity<Void> addStateToMultipleSurveyUnits(
            @RequestBody @Valid SurveyUnitsNewStateRequest request) {

        surveyUnitStatePort.addStateToMultipleSurveyUnits(request.getSurveyUnitIds(), request.getStateType());
        return ResponseEntity.noContent().build();
    }
}
