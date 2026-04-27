package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.api.reporting.presenter.InterviewerCampaignsProgressPresenter;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsReportingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InterviewerCampaignsProgressCsvExporter extends AbstractCsvExporter {

    private final InterviewerCampaignsProgressPresenter presenter;
    private final InterviewerCampaignsReportingPort campaignReportingPort;

    public ResponseEntity<byte[]> export(String userId, String interviewerId, LocalDate date) {
        List<InterviewerCampaignsProgressResponse> data =
                campaignReportingPort.getCampaignsStatsForInterviewer(userId, date, interviewerId, presenter);
        InterviewerCampaignsProgressCsv csv = InterviewerCampaignsProgressCsv.from(data);
        return buildResponse(csv, interviewerId + "_Avancement", date);
    }
}
