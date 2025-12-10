package fr.insee.pearljam.api.campaign.controller;

import fr.insee.pearljam.api.campaign.dto.output.CommunicationTemplateResponseDto;
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.port.userside.CommunicationTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
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
public class CommunicationTemplateController {
    private final CommunicationTemplateService communicationTemplateService;

    /**
     * This method returns the list of visibilities associated with the
     * campaign {id}
     *
     * @param campaignId campaign id
     * @return List of {@link CommunicationTemplateResponseDto}
     */
    @Operation(summary = "Get campaign communication templates")
    @GetMapping(path = Constants.API_CAMPAIGN_ID_COMMUNICATION_TEMPLATES)
    public List<CommunicationTemplateResponseDto> getCommunicationTemplates(
            @NotBlank @PathVariable(value = "id") String campaignId) {
        List<CommunicationTemplate> communicationTemplates = communicationTemplateService.findCommunicationTemplates(campaignId);
        return CommunicationTemplateResponseDto.fromModel(communicationTemplates);
    }
}
