package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class CampaignCollectionController {
    private final CampaignReportingPort campaignReportingService;
    private final CampaignCollectionPresenter presenter;

    @Operation(summary = "Get campaigns reporting")
    @GetMapping(Constants.API_REPORTING_CAMPAIGNS_COLLECTION)
    @Parameter(name = "userId", hidden = true)
    public List<CampaignCollectionResponse> getCampaignsCollect(
            @RequestParam(required = false) LocalDate day,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {
        return campaignReportingService.getCampaignsStats(userId, day, presenter);
    }
}
