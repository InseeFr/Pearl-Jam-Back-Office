package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.progress.CampaignProgressCsvExporter;
import fr.insee.pearljam.contracts.constants.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class CampaignProgressExportController {

    private final CampaignProgressCsvExporter csvExporter;

    @Operation(summary = "Export campaigns progress as CSV file")
    @GetMapping(Constants.API_REPORTING_CAMPAIGNS_PROGRESS_EXPORT)
    @Parameter(name = "userId", hidden = true)
    public ResponseEntity<byte[]> exportCampaignsProgressAsCsv(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @CurrentSecurityContext(expression = "authentication.name") String userId) {

        return csvExporter.export(userId, date);
    }
}
