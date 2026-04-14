package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.campaign.presenter.CampaignOrganizationPresenter;
import fr.insee.pearljam.api.campaign.response.CampaignOrganizationResponse;
import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationPort;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static fr.insee.pearljam.contracts.constants.Constants.API_CAMPAIGN_ORGANIZATION;

@RestController
@AllArgsConstructor
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class CampaignOrganizationController {

    private final CampaignOrganizationPort campaignOrganizationPort;
    private final CampaignOrganizationPresenter presenter;


    @Operation(summary = "Get campaign organization")
    @Parameter(name = "userId", hidden = true)
    @GetMapping(path = API_CAMPAIGN_ORGANIZATION)
    public CampaignOrganizationResponse getCampaignOrganization(
            @PathVariable @NotBlank String id,
            @CurrentSecurityContext(expression = "authentication.name") String userId)
            throws CampaignNotFoundException {
        return campaignOrganizationPort.getCampaignOrganization(userId, id, presenter);
    }
}
