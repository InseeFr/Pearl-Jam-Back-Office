package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionByInterviewersPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByInterviewersResponse;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
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
public class CampaignCollectionByInterviewerController {
    private final CampaignReportingByInterviewersPort reportingByInterviewersPort;
    private final CampaignCollectionByInterviewersPresenter presenter;

    @Operation(summary = "Get campaign collection for each interviewer from daily stats snapshot")
    @Parameter(name = "userId", hidden = true)
    @GetMapping(Constants.API_REPORTING_INTERVIEWERS_COLLECTION)
    public CampaignCollectionByInterviewersResponse getCampaignProgressForInterviewersFromStats(
            @PathVariable(value = "campaignId") @NotBlank String campaignId,
            @CurrentSecurityContext(expression = "authentication.name") String userId,
            @RequestParam(required = false) LocalDate day) throws CampaignNotFoundException {

        return reportingByInterviewersPort.getProgressForDay(userId, campaignId, day, presenter);
    }
}
