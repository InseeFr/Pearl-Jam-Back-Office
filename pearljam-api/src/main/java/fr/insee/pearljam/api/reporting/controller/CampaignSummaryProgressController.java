package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignSummaryProgress;
import fr.insee.pearljam.domain.reporting.service.CampaignSummaryProgressService;
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

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
@Validated
public class CampaignSummaryProgressController {
    private final CampaignSummaryProgressService campaignSummaryProgressService;
    private final Clock clock;

    @Operation(summary = "Get summary of campaigns (based on my preferences), including state counts")
    @GetMapping(Constants.API_REPORTING_CAMPAIGNS_SUMMARY)
    @Parameter(name = "userId", hidden = true)
    public List<CampaignSummaryProgress> getCampaignSummaryWithStateCount(
            @RequestParam(required = false) LocalDate day,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {
        LocalDate now = LocalDate.now(clock);
        if (day == null || day.isAfter(now)) {
            day = now;
        }
        return campaignSummaryProgressService.getCampaignSummaryProgress(userId, day);
    }
}

