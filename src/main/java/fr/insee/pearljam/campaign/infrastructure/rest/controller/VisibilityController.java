package fr.insee.pearljam.campaign.infrastructure.rest.controller;

import java.util.List;

import fr.insee.pearljam.campaign.infrastructure.rest.dto.output.VisibilityCampaignDto;
import fr.insee.pearljam.campaign.infrastructure.rest.dto.input.VisibilityUpdateDto;
import fr.insee.pearljam.campaign.domain.port.userside.VisibilityService;
import fr.insee.pearljam.campaign.domain.service.exception.VisibilityHasInvalidDatesException;
import fr.insee.pearljam.campaign.domain.model.Visibility;
import fr.insee.pearljam.campaign.domain.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.campaign.domain.service.exception.VisibilityNotFoundException;
import fr.insee.pearljam.configuration.web.Constants;
import fr.insee.pearljam.security.domain.port.userside.AuthenticatedUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "01. Campaigns", description = "Endpoints for campaigns")
@Slf4j
@RequiredArgsConstructor
@Validated
public class VisibilityController {
    private final VisibilityService visibilityService;
    private final AuthenticatedUserService authenticatedUserService;

    /**
     * This method returns the list of visibilities associated with the
     * campaign {id}
     *
     * @param campaignId campaign id
     * @return List of {@link VisibilityCampaignDto}
     */
    @Operation(summary = "Get campaign visibilities")
    @GetMapping(path = Constants.API_CAMPAIGN_ID_VISIBILITIES)
    public List<VisibilityCampaignDto> getVisibilities(@NotBlank @PathVariable(value = "id") String campaignId) throws CampaignNotFoundException {
        String userId = authenticatedUserService.getCurrentUserId();
        log.info("{} try to get campaign[{}] visibilities ", userId, campaignId);

        List<Visibility> visibilities = visibilityService.findVisibilities(campaignId);
        return VisibilityCampaignDto.fromModel(visibilities);
    }

    /**
     * Update the visibility dates for a given campaign and organizational unit
     *
     * @param visibilityToUpdate the visibility to update
     * @param idCampaign campaign identifier
     * @param idOu organizational unit id
     * @throws VisibilityNotFoundException if the visibility does not exist
     */
    @Operation(summary = "Update the visibility of a campaign for an organizational unit")
    @PutMapping(path = Constants.API_CAMPAIGN_ID_OU_ID_VISIBILITY)
    public void updateVisibility(
            @Valid @NotNull @RequestBody VisibilityUpdateDto visibilityToUpdate,
            @NotBlank @PathVariable(value = "idCampaign") String idCampaign,
            @NotBlank @PathVariable(value = "idOu") String idOu) throws VisibilityNotFoundException, VisibilityHasInvalidDatesException {
        String userId = authenticatedUserService.getCurrentUserId();
        log.info("{} try to change OU[{}] visibility on campaign[{}] ", userId, idOu, idCampaign);
        visibilityService.updateVisibility(VisibilityUpdateDto.toModel(visibilityToUpdate, idCampaign, idOu));
        log.info("Visibility with CampaignId {} for Organizational Unit {} updated", idCampaign,
                idOu);
    }
}
