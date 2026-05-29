package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsReportingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InterviewerCampaignsProgressCsvExporter extends AbstractCsvExporter {

    private final InterviewerCampaignsProgressCsvPresenter presenter;
    private final InterviewerCampaignsReportingPort campaignReportingPort;

    public ResponseEntity<byte[]> export(String userId, String interviewerId, LocalDate date) {
        InterviewerCampaignsProgressCsv csv =
                campaignReportingPort.getCampaignsStatsForInterviewer(userId, date, interviewerId, presenter);
        return buildResponse(csv, interviewerId + "_Avancement", date);
    }
}
