package fr.insee.pearljam.api.reporting.controller;

import java.time.LocalDate;
import java.util.List;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.api.reporting.presenter.CampaignProgressPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class CampaignProgressController {
    private final CampaignReportingPort campaignReportingService;
    private final CampaignProgressPresenter presenter;

    @Operation(summary = "Get campaigns reporting")
    @GetMapping(Constants.API_REPORTING_CAMPAIGNS_PROGRESS)
    @Parameter(name = "userId", hidden = true)
    public List<CampaignProgressResponse> getCampaignsProgress(
            @RequestParam(required = false) LocalDate day,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {
        return campaignReportingService.getCampaignsStats(userId, day, presenter);
    }
}
