package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.InterviewerCampaignsClosingCausesPresenter;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsReportingPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static fr.insee.pearljam.contracts.constants.Constants.API_REPORTING_INTERVIEWER_CLOSING_CAUSES_BY_CAMPAIGNS;

@RestController
@RequiredArgsConstructor
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class InterviewerCampaignsClosingCauses {
    private final InterviewerCampaignsReportingPort interviewerCampaignsReportingPort;
    private final InterviewerCampaignsClosingCausesPresenter presenter;

    @Operation(summary = "Endpoint for retrieving closing causes for interviewer campaigns")
    @GetMapping(API_REPORTING_INTERVIEWER_CLOSING_CAUSES_BY_CAMPAIGNS)
    @Parameter(name = "userId", hidden = true)
    public List<InterviewerCampaignsClosingCausesResponse> getInterviewerClosingCausesByCampaign(
            @PathVariable(value = "interviewerId") @NotBlank String interviewerId,
            @RequestParam(required = false) LocalDate day,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {

        return interviewerCampaignsReportingPort.getCampaignsStatsForInterviewer(userId, day, interviewerId, presenter);
    }
}
