package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.controller.request.CloseSurveyUnitsRequest;
import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitClosingApiPagePresenter;
import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitClosingApiPresenter;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToCloseResponse;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
public class SurveyUnitClosingController {

    private final SurveyUnitClosingPort surveyUnitClosingPort;
    private final SurveyUnitClosingApiPresenter presenter;
    private final SurveyUnitClosingApiPagePresenter pagePresenter;

    /**
     * Add closing cause to multiple survey units
     */
    @PostMapping(Constants.API_SURVEYUNIT_CLOSE_SURVEYUNITS)
    public ResponseEntity<Void> addClosingCauseToMultipleSurveyUnits(
            @RequestBody @Valid CloseSurveyUnitsRequest request) {

        surveyUnitClosingPort.addClosingCauseToMultipleSurveyUnits(request.getSurveyUnitIds(), request.getClosingCauseType(), request.getToClose());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get survey units to close for management UI")
    @GetMapping(Constants.API_SURVEYUNITS_TO_CLOSE)
    @Parameter(name = "userId", hidden = true)
    public List<SurveyUnitToCloseResponse> getSurveyUnitsToClose(
            @CurrentSecurityContext(expression = "authentication.name") String userId) {
        return surveyUnitClosingPort.getSurveyUnitsToClose(userId, presenter);
    }

    @Operation(summary = "Get survey units to close for management UI with pagination")
    @GetMapping(value = Constants.API_SURVEYUNITS_TO_CLOSE, params = {"page", "size"})
    @Parameter(name = "userId", hidden = true)
    public ResponseEntity<Page<SurveyUnitToCloseResponse>> getSurveyUnitsToClosePaginated(
            @CurrentSecurityContext(expression = "authentication.name") String userId,
            Pageable pageable) {
        Page<SurveyUnitToCloseResponse> result = surveyUnitClosingPort
                .getSurveyUnitsToClose(userId, pagePresenter, pageable);
        return ResponseEntity.ok(result);
    }
}
