package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.api.reporting.presenter.InterviewerCampaignsCollectionPresenter;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignCollectionResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsReportingPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class InterviewerCampaignsCollectionController {
    private final InterviewerCampaignsReportingPort interviewerCampaignsReportingService;
    private final InterviewerCampaignsCollectionPresenter presenter;

    @Operation(summary = "Get interviewer campaigns reporting")
    @GetMapping(Constants.API_REPORTING_INTERVIEWER_CAMPAIGNS_COLLECTION)
    @Parameter(name = "userId", hidden = true)
    public List<InterviewerCampaignCollectionResponse> getInterviewerCampaignsCollection(
            @PathVariable String interviewerId,
            @RequestParam(required = false) LocalDate day,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {
        return interviewerCampaignsReportingService.getCampaignsStatsForInterviewer(userId, day, interviewerId, presenter);
    }
}
