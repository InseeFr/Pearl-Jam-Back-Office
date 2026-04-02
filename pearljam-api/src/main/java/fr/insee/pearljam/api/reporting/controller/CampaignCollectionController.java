package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.reporting.readmodel.collect.CampaignCollection;
import fr.insee.pearljam.domain.reporting.service.CampaignCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class CampaignCollectionController {
    private final CampaignCollectionService campaignCollectionService;
    private final Clock clock;

    @Operation(summary = "Get campaigns reporting")
    @GetMapping(Constants.API_REPORTING_CAMPAIGNS_COLLECTION)
    @Parameter(name = "userId", hidden = true)
    public List<CampaignCollection> getCampaignsCollect(
            @RequestParam(required = false) LocalDate day,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {
        LocalDate now = LocalDate.now(clock);
        if (day == null || day.isAfter(now)) {
            day = now;
        }
        return campaignCollectionService.getCampaignsCollection(userId, day);
    }
}

