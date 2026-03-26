package fr.insee.pearljam.api.reporting.controller;

import java.time.Instant;
import java.util.List;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.reporting.model.CampaignProgression;
import fr.insee.pearljam.domain.reporting.service.CampaignProgressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class CampaignProgressionController {
    private final CampaignProgressionService campaignProgressionService;

    @Operation(summary = "Get campaigns reporting")
    @GetMapping(Constants.API_REPORTING_CAMPAIGNS_PROGRESS)
    @Parameter(name = "userId", hidden = true)
    public List<CampaignProgression> getCampaignsProgression(
            @RequestParam Instant date,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {

        return campaignProgressionService.getCampaignsProgression(userId, date);
    }
}

