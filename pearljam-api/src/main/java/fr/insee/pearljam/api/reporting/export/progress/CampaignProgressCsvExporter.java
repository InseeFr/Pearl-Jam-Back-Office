package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CampaignProgressCsvExporter extends AbstractCsvExporter {

    private final CampaignProgressCsvPresenter presenter;
    private final CampaignReportingPort campaignReportingPort;

    public ResponseEntity<byte[]> export(String userId, LocalDate date) {
        CampaignProgressCsv csv = campaignReportingPort.getCampaignsStats(userId, date, presenter);
        return buildResponse(csv, userId + "_Avancement_enquetes", date);
    }
}
