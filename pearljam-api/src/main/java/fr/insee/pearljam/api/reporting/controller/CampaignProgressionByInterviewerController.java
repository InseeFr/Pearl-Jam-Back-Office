package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;

import fr.insee.pearljam.domain.reporting.port.in.CampaignProgressionByInterviewersPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignProgressionByInterviewers;
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

import java.time.Clock;
import java.time.LocalDate;

@RestController
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CampaignProgressionByInterviewerController {
    private final CampaignProgressionByInterviewersPort progressionByInterviewersPort;
    private final Clock clock;

    @Operation(summary = "Get campaign progression for each interviewer from daily stats snapshot")
    @Parameter(name = "userId", hidden = true)
    @GetMapping(Constants.API_REPORTING_INTERVIEWERS_PROGRESS)
    public CampaignProgressionByInterviewers getCampaignProgressionForInterviewersFromStats(
            @PathVariable(value = "campaignId") @NotBlank String campaignId,
            @CurrentSecurityContext(expression = "authentication.name") String userId,
            @RequestParam(required = false) LocalDate day) throws CampaignNotFoundException {

        LocalDate now = LocalDate.now(clock);
        if (day == null || day.isAfter(now)) {
            day = now;
        }
        return progressionByInterviewersPort.getProgressionForDay(userId, campaignId, day);
    }
}

