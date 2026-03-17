package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateCountDto;
import fr.insee.pearljam.domain.reporting.service.CampaignProgressionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CampaignProgressionController {
    private final CampaignProgressionService campaignProgressionService;

    @Operation(summary = "GET campaign progress status")
    @GetMapping(Constants.API_REPORTING_CAMPAIGNS_PROGRESS)
    public List<CampaignProgressionDto> getCampaignsProgression(
            @RequestParam(required = false, name = "date") Instant instant,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {
        if (instant == null) {
            instant = Instant.now();
        }
        return campaignProgressionService.getCampaignsProgression(userId, instant);
    }
}
