package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.SurveyUnitToReviewPresenter;
import fr.insee.pearljam.api.reporting.response.SurveyUnitToReviewResponse;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.reporting.port.in.SurveyUnitToReviewPort;
import fr.insee.pearljam.domain.security.port.in.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
@Slf4j
@RequiredArgsConstructor
@Validated
public class SurveyUnitToReviewController {

    private final SurveyUnitToReviewPort surveyUnitToReviewPort;
    private final SurveyUnitToReviewPresenter presenter;
    private final AuthenticatedUserService authenticatedUserService;

    @Operation(summary = "Get survey units to review with pagination",
            description = "Returns a paginated list of survey units that need to be reviewed")
    @Parameter(name = "userId", hidden = true)
    @GetMapping(Constants.API_REPORTING_SURVEY_UNITS_TO_REVIEW)
    public SurveyUnitToReviewResponse getSurveyUnitsToReview(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) String search) {

        String userId = authenticatedUserService.getCurrentUserId();
        log.info("Fetching survey units to review for user {} with search: {}", userId, search);

        return surveyUnitToReviewPort.getSurveyUnitsToReview(userId, search, pageable, presenter);
    }
}