package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.reporting.model.CampaignProgression;
import fr.insee.pearljam.domain.reporting.model.CampaignSummaryWithStateCount;
import fr.insee.pearljam.domain.reporting.service.CampaignSummaryWithStateCountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class CampaignSummaryWithStateCountController {
    private final CampaignSummaryWithStateCountService campaignSummaryWithStateCountService;

    @Operation(summary = "Get summary of campaigns (based on my preferences), including state counts")
    @GetMapping(Constants.API_REPORTING_CAMPAIGNS_SUMMARY_STATE_COUNTS)
    @Parameter(name = "userId", hidden = true)
    public List<CampaignSummaryWithStateCount> getCampaignSummaryWithStateCount(
            @CurrentSecurityContext(expression = "authentication.name") String userId) {

        return campaignSummaryWithStateCountService.getCampaignSummaryWithStateCount(userId);
    }
}

