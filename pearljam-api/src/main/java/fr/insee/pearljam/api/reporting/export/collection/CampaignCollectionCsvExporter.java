package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CampaignCollectionCsvExporter extends AbstractCsvExporter {

    private final CampaignCollectionPresenter presenter;
    private final CampaignReportingPort campaignReportingPort;

    public ResponseEntity<byte[]> export(String userId, LocalDate date) {
        List<CampaignCollectionResponse> data = campaignReportingPort.getCampaignsStats(userId, date, presenter);
        CampaignCollectionCsv csv = CampaignCollectionCsv.from(data);
        return buildResponse(csv, "Avancement_collecte_enquetes", date);
    }
}
