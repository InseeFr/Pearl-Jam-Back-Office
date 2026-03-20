package fr.insee.pearljam.api.reporting.controller;

import java.time.Instant;
import java.util.List;

import fr.insee.pearljam.contracts.campaign.dto.CampaignProgressionDto;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.campaign.model.CampaignProgressionProjection;
import fr.insee.pearljam.domain.reporting.service.CampaignProgressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
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
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class CampaignProgressionController {
    private final CampaignProgressionService campaignProgressionService;
    private final ModelMapper modelMapper;

    public record CampaignProgressionRequest(@NotNull Instant date, String campaignLabel) {}

    @Operation(summary = "POST campaign reporting")
    @PostMapping(Constants.API_REPORTING_CAMPAIGNS_PROGRESS)
    public List<CampaignProgressionDto> getCampaignsProgression(
            @RequestBody CampaignProgressionRequest request,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {

        List<CampaignProgressionProjection> projections = campaignProgressionService
                .getCampaignsProgression(userId, request.date);

        return projections.stream()
                .map(projection -> modelMapper.map(projection, CampaignProgressionDto.class))
                .toList();
    }
}

