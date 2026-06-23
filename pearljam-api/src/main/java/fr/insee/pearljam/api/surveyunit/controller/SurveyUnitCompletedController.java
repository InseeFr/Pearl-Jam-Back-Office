package fr.insee.pearljam.api.surveyunit.controller;

import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitCompletedApiPresenter;
import fr.insee.pearljam.api.surveyunit.response.SurveyUnitCompletedPageResponse;
import fr.insee.pearljam.domain.surveyunit.port.in.application.SurveyUnitCompletedPort;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static fr.insee.pearljam.contracts.constants.Constants.API_CAMPAIGN_SU_COMPLETED;

@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@Tag(name = "02. Survey-units", description = "Endpoints for survey-units")
public class SurveyUnitCompletedController {

    private final SurveyUnitCompletedPort surveyUnitCompletedPort;
    private final SurveyUnitCompletedApiPresenter surveyUnitCompletedApiPresenter;

    /**
     * Get survey units with CLO/FIN state for a specific campaign
     */
    @GetMapping(API_CAMPAIGN_SU_COMPLETED)
    @Parameter(name = "userId", hidden = true)
    public SurveyUnitCompletedPageResponse getCompletedSurveyUnitsForCampaign(
            @PathVariable @NotBlank String id,
            @ParameterObject Pageable pageable,
            @CurrentSecurityContext(expression = "authentication.name") String userId,
            @RequestParam(required = false) String search)
    {
        log.info("Fetching survey units completed with search: {}", search);
        return surveyUnitCompletedPort.getCompletedSurveyUnits(userId, id, search, pageable, surveyUnitCompletedApiPresenter);
    }
}