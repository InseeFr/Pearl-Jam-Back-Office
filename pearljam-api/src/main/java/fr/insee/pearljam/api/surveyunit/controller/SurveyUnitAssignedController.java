package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitAssignedApiPresenter;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitAssignedPageResponse;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitAssignedPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
@Slf4j
@RequiredArgsConstructor
@Validated
public class SurveyUnitAssignedController {

    private final SurveyUnitAssignedPort surveyUnitAssignedPort;
    private final SurveyUnitAssignedApiPresenter presenter;

    @Operation(summary = "Get survey units assigned with pagination",
        description = "Returns a paginated list of survey units assigned to the organization-unit of the user")
    @Parameter(name = "userId", hidden = true)
    @GetMapping(Constants.API_SURVEY_UNITS_ASSIGNED)
    public SurveyUnitAssignedPageResponse getSurveyUnitsAssigned(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String campaignId,
        Pageable pageable,
        @CurrentSecurityContext(expression = "authentication.name") String userId) {

        log.info("Fetching survey units to review for user {} with search: {}", userId, search);

        return surveyUnitAssignedPort.getSurveyUnitsAssigned(userId, campaignId, search, pageable, presenter);
    }
}