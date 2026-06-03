package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.controller.presenter.SurveyUnitHistoryApiPresenter;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitHistoryResponse;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitHistoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
@Slf4j
@RequiredArgsConstructor
@Validated
public class SurveyUnitHistoryController {

    private final SurveyUnitHistoryPort surveyUnitHistoryPort;
    private final SurveyUnitHistoryApiPresenter presenter;

    @Operation(summary = "Get survey unit history",
            description = "Return states and communications of a survey unit")
    @Parameter(name = "userId", hidden = true)
    @GetMapping(Constants.API_SURVEYUNIT_HISTORY)
    public SurveyUnitHistoryResponse getSurveyUnitHistory(
            @PathVariable(value = "surveyUnitId") String surveyUnitId,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {

        log.info("Get survey unit history for user {} and survey unit : {}", userId, surveyUnitId);
        return surveyUnitHistoryPort.getSurveyUnitHistory(surveyUnitId, presenter);
    }
}