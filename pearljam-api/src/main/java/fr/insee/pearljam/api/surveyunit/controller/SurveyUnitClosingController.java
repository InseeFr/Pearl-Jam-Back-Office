package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.controller.request.CloseSurveyUnitsRequest;
import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitToClosePresenter;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToCloseResponse;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fr.insee.pearljam.contracts.constants.Constants.API_SURVEYUNITS_CLOSE;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
public class SurveyUnitClosingController {

    private final SurveyUnitClosingPort surveyUnitClosingPort;
    private final SurveyUnitToClosePresenter presenter;

    /**
     * Add closing cause to multiple survey units
     */
    @PostMapping(API_SURVEYUNITS_CLOSE)
    public ResponseEntity<Void> addClosingCauseToMultipleSurveyUnits(
            @RequestBody @Valid CloseSurveyUnitsRequest request) {

        surveyUnitClosingPort.addClosingCauseToMultipleSurveyUnits(request.getSurveyUnitIds(), request.getClosingCauseType());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get survey units to close for management UI")
    @GetMapping(Constants.API_SURVEYUNITS_TO_CLOSE)
    @Parameter(name = "userId", hidden = true)
    public List<SurveyUnitToCloseResponse> getSurveyUnitsToClose(
            @CurrentSecurityContext(expression = "authentication.name") String userId) {
        return surveyUnitClosingPort.getSurveyUnitsToClose(userId, presenter);
    }
}
