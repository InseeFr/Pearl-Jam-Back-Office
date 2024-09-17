package fr.insee.pearljam.api.campaign.controller;

import fr.insee.pearljam.api.campaign.dto.input.CommunicationInformationUpdateDto;
import fr.insee.pearljam.api.campaign.dto.output.CommunicationInformationResponseDto;
import fr.insee.pearljam.api.campaign.dto.output.VisibilityCampaignDto;
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;
import fr.insee.pearljam.domain.campaign.port.userside.CommunicationInformationService;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "01. Campaigns", description = "Endpoints for campaigns")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CommunicationInformationController {
    private final CommunicationInformationService communicationInformationService;
    private final AuthenticatedUserService authenticatedUserService;

    /**
     * This method returns the list of communication informations associated with the
     * campaign {id}
     *
     * @param campaignId campaign id
     * @return List of {@link VisibilityCampaignDto}
     */
    @Operation(summary = "Get campaign communication informations")
    @GetMapping(path = Constants.API_CAMPAIGN_ID_COMMUNICATION_INFORMATIONS)
    public List<CommunicationInformationResponseDto> getCommunicationInformations(@NotBlank @PathVariable(value = "id") String campaignId) throws CampaignNotFoundException {
        String userId = authenticatedUserService.getCurrentUserId();
        log.info("{} try to get campaign[{}] communication informations ", userId, campaignId);

        List<CommunicationInformation> communicationInformations = communicationInformationService.findCommunicationInformations(campaignId);
        return CommunicationInformationResponseDto.fromModel(communicationInformations);
    }

    /**
     * Update the communication information for a given campaign and organizational unit
     *
     * @param communicationInformationToUpdate the communication information to update
     * @param idCampaign campaign identifier
     * @param idOu organizational unit id
     */
    @Operation(summary = "Update the communication information of a campaign for an organizational unit")
    @PutMapping(path = Constants.API_CAMPAIGN_ID_OU_ID_COMMUNICATION_INFORMATION)
    public void updateCommunicationInformation(
            @Valid @NotNull @RequestBody CommunicationInformationUpdateDto communicationInformationToUpdate,
            @NotBlank @PathVariable(value = "idCampaign") String idCampaign,
            @NotBlank @PathVariable(value = "idOu") String idOu) {
        String userId = authenticatedUserService.getCurrentUserId();
        log.info("{} try to change OU[{}] communication information on campaign[{}] ", userId, idOu, idCampaign);
        communicationInformationService
                .updateCommunicationInformation(CommunicationInformationUpdateDto.toModel(
                        communicationInformationToUpdate,
                        idCampaign,
                        idOu));
        log.info("Communication information with CampaignId {} for Organizational Unit {} updated", idCampaign,
                idOu);
    }
}
