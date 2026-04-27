package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.api.reporting.presenter.CampaignProgressByInterviewersPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressByInterviewersResponse;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InterviewerProgressCsvExporter extends AbstractCsvExporter {

    private final CampaignProgressByInterviewersPresenter presenter;
    private final CampaignReportingByInterviewersPort campaignReportingByInterviewersPort;

    public ResponseEntity<byte[]> export(String userId, String campaignId, LocalDate date)
            throws CampaignNotFoundException {
        CampaignProgressByInterviewersResponse data =
                campaignReportingByInterviewersPort.getProgressForDay(userId, campaignId, date, presenter);
        InterviewerProgressCsv csv = InterviewerProgressCsv.from(data);
        return buildResponse(csv, campaignId + "_Avancement_enqueteurs", date);
    }
}
