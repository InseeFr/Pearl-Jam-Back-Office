package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.reporting.port.in.CampaignProgressSnapshotServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Tag(name = "13. Reporting", description = "Endpoints for reporting")
@Validated
public class CampaignReportingSnapshotController {

    private final CampaignProgressSnapshotServicePort snapshotService;
    private final Clock clock;

    @Operation(summary = "Trigger snapshot computation for a given day (admin only)")
    @PostMapping(Constants.API_ADMIN_REPORTING_SNAPSHOT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void computeSnapshot(@RequestParam @NotNull LocalDate date) {
        if (date.isAfter(LocalDate.now(clock))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date must not be in the future");
        }
        snapshotService.computeSnapshot(date);
    }
}
