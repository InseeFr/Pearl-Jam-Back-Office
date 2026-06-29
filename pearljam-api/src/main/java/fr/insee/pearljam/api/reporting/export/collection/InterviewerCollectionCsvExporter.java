package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InterviewerCollectionCsvExporter extends AbstractCsvExporter {

    private final InterviewerCollectionCsvPresenter presenter;
    private final CampaignReportingByInterviewersPort campaignReportingByInterviewersPort;

    public ResponseEntity<byte[]> export(String userId, String campaignId, LocalDate date) {
        InterviewerCollectionCsv csv =
                campaignReportingByInterviewersPort.getProgressForDay(userId, campaignId, date, presenter);
        return buildResponse(csv, campaignId + "_Avancement_collecte_enqueteurs", date);
    }
}
