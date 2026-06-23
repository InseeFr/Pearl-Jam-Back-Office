package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.progress.InterviewerProgressCsvExporter;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
public class InterviewerProgressExportController {

    private final InterviewerProgressCsvExporter csvExporter;

    @Operation(summary = "Export campaign progress by interviewers as CSV file")
    @GetMapping(Constants.API_REPORTING_INTERVIEWERS_PROGRESS_EXPORT)
    @Parameter(name = "userId", hidden = true)
    public ResponseEntity<byte[]> exportInterviewerProgressAsCsv(
            @PathVariable(value = "campaignId") @NotBlank String campaignId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @CurrentSecurityContext(expression = "authentication.name") String userId)
            throws CampaignNotFoundException {

        return csvExporter.export(userId, campaignId, date);
    }
}
