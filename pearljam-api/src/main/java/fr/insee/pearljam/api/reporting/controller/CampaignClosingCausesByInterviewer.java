package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.CampaignClosingCausesByInterviewerPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse;
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

import static fr.insee.pearljam.contracts.constants.Constants.API_REPORTING_CLOSING_CAUSES_BY_INTERVIEWERS;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
@Validated
public class CampaignClosingCausesByInterviewer {

    private final CampaignReportingByInterviewersPort reportingByInterviewersPort;
    private final CampaignClosingCausesByInterviewerPresenter presenter;


    @Operation(summary = "")
    @GetMapping(API_REPORTING_CLOSING_CAUSES_BY_INTERVIEWERS)
    @Parameter(name = "userId", hidden = true)
    public CampaignClosingCausesByInterviewersResponse getCampaignClosingCausesStatusByInterviewer(
            @PathVariable(value = "campaignId") @NotBlank String campaignId,
            @RequestParam(required = false) LocalDate day,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {

        return reportingByInterviewersPort.getProgressForDay(userId, campaignId, day, presenter);
    }
}
