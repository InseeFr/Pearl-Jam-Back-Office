package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.domain.campaign.model.CampaignOrganization;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.service.CampaignOrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static fr.insee.pearljam.contracts.constants.Constants.API_CAMPAIGN_ORGANIZATION;

@Controller
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class CampaignOrganizationController {

    CampaignOrganizationService campaignOrganizationService;

    @Operation(summary = "Get campaign organization")
    @GetMapping(path = API_CAMPAIGN_ORGANIZATION)
    public CampaignOrganization getCampaign(
            @NotBlank @PathVariable(value = "id") String campaignId,
            @CurrentSecurityContext(expression = "authentication.name") String userId)
            throws CampaignNotFoundException {
        return campaignOrganizationService.getCampaignOrganizations(userId, campaignId);
    }
}
