package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.api.reporting.presenter.InterviewerCampaignsCollectionPresenter;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignCollectionResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsReportingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InterviewerCampaignsCollectionCsvExporter extends AbstractCsvExporter {

    private final InterviewerCampaignsCollectionPresenter presenter;
    private final InterviewerCampaignsReportingPort campaignReportingPort;

    public ResponseEntity<byte[]> export(String userId, String interviewerId, LocalDate date) {
        List<InterviewerCampaignCollectionResponse> data =
                campaignReportingPort.getCampaignsStatsForInterviewer(userId, date, interviewerId, presenter);
        InterviewerCampaignsCollectionCsv csv = InterviewerCampaignsCollectionCsv.from(data);
        return buildResponse(csv, interviewerId + "_Avancement_collecte", date);
    }
}
