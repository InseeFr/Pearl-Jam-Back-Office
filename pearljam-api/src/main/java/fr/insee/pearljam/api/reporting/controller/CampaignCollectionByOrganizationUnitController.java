package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionByOrganizationUnitsPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByOrganizationUnitsResponse;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByOrganizationUnitsPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CampaignCollectionByOrganizationUnitController {
    private final CampaignReportingByOrganizationUnitsPort reportingByOrganizationUnitsPort;
    private final CampaignCollectionByOrganizationUnitsPresenter presenter;

    @Operation(summary = "Get campaign collection for each organization unit from daily stats snapshot")
    @Parameter(name = "userId", hidden = true)
    @GetMapping(Constants.API_REPORTING_OUS_COLLECTION)
    public CampaignCollectionByOrganizationUnitsResponse getCampaignProgressForOUsFromStats(
            @PathVariable(value = "campaignId") @NotBlank String campaignId,
            @CurrentSecurityContext(expression = "authentication.name") String userId,
            @RequestParam(required = false) LocalDate day) throws CampaignNotFoundException {

        return reportingByOrganizationUnitsPort.getProgressForDay(userId, campaignId, day, presenter);
    }
}
