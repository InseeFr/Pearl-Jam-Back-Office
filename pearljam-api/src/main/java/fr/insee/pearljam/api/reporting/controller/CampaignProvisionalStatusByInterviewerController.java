package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.CampaignProvisionalStatusByInterviewerApiPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProvisionalStatusByInterviewersResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
@Validated
public class CampaignProvisionalStatusByInterviewerController {

    private final CampaignReportingByInterviewersPort reportingByInterviewersPort;
    private final CampaignProvisionalStatusByInterviewerApiPresenter presenter;


    @Operation(summary = "")
    @GetMapping("/api/reporting/campaigns/{campaign}/interviewers/closing-causes")
    @Parameter(name = "userId", hidden = true)
    public List<CampaignProvisionalStatusByInterviewersResponse> getCampaignProvisionalStatusByInterviewer(
            @RequestParam String campaignId,
            @RequestParam(required = false) LocalDate day,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {

        return reportingByInterviewersPort.getProgressForDay(userId, campaignId, day, presenter);
    }
}
