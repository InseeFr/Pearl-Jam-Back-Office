package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CampaignCollectionCsvExporter extends AbstractCsvExporter {

    private final CampaignCollectionCsvPresenter presenter;
    private final CampaignReportingPort campaignReportingPort;

    public ResponseEntity<byte[]> export(String userId, LocalDate date) {
        CampaignCollectionCsv csv = campaignReportingPort.getCampaignsStats(userId, date, presenter);
        return buildResponse(csv, "Avancement_collecte_enquetes", date);
    }
}
