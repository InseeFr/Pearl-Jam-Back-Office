package fr.insee.pearljam.api.reporting.controller;

import java.time.Instant;
import java.util.List;

import fr.insee.pearljam.contracts.campaign.dto.CampaignProgressionDto;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.campaign.model.CampaignProgressionProjection;
import fr.insee.pearljam.domain.reporting.service.CampaignProgressionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CampaignProgressionController {
    private final CampaignProgressionService campaignProgressionService;
    private final ModelMapper modelMapper;

    @Operation(summary = "GET campaign progress status")
    @PostMapping(Constants.API_REPORTING_CAMPAIGNS_PROGRESS)
    public List<CampaignProgressionDto> getCampaignsProgression(
            @RequestBody Instant date,
            @RequestBody(required = false) String campaignLabel,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {

        List<CampaignProgressionProjection> projections = campaignProgressionService
                .getCampaignsProgression(userId, date);

        return projections.stream()
                .map(projection -> modelMapper.map(projection, CampaignProgressionDto.class))
                .toList();
    }
}

