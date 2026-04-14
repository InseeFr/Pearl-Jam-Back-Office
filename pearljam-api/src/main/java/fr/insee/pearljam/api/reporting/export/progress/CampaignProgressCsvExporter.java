package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.api.reporting.presenter.CampaignProgressPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CampaignProgressCsvExporter extends AbstractCsvExporter {

    private final CampaignProgressPresenter presenter;
    private final CampaignReportingPort campaignReportingPort;

    public ResponseEntity<byte[]> export(String userId, LocalDate date) {
        List<CampaignProgressResponse> data = campaignReportingPort.getCampaignsStats(userId, date, presenter);
        CampaignProgressCsv csv = CampaignProgressCsv.from(data);
        return buildResponse(csv, userId, date);
    }

    @Override
    protected String getExportLabel() {
        return "Avancement_enquetes";
    }
}
