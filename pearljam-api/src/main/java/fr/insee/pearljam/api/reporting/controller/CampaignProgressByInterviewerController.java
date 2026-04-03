package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.presenter.CampaignProgressByInterviewersPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressByInterviewersResponse;
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
public class CampaignProgressByInterviewerController {
    private final CampaignReportingByInterviewersPort progressByInterviewersPort;
    private final CampaignProgressByInterviewersPresenter presenter;

    @Operation(summary = "Get campaign progress for each interviewer from daily stats snapshot")
    @Parameter(name = "userId", hidden = true)
    @GetMapping(Constants.API_REPORTING_INTERVIEWERS_PROGRESS)
    public CampaignProgressByInterviewersResponse getCampaignProgressForInterviewersFromStats(
            @PathVariable(value = "campaignId") @NotBlank String campaignId,
            @CurrentSecurityContext(expression = "authentication.name") String userId,
            @RequestParam(required = false) LocalDate day) throws CampaignNotFoundException {

        return progressByInterviewersPort.getProgressForDay(userId, campaignId, day, presenter);
    }
}
