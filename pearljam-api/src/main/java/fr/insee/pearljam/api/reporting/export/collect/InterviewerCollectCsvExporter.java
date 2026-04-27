package fr.insee.pearljam.api.reporting.export.collect;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionByInterviewersPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByInterviewersResponse;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InterviewerCollectCsvExporter extends AbstractCsvExporter {

    private final CampaignCollectionByInterviewersPresenter presenter;
    private final CampaignReportingByInterviewersPort campaignReportingByInterviewersPort;

    public ResponseEntity<byte[]> export(String userId, String campaignId, LocalDate date)
            throws CampaignNotFoundException {
        CampaignCollectionByInterviewersResponse data =
                campaignReportingByInterviewersPort.getProgressForDay(userId, campaignId, date, presenter);
        InterviewerCollectCsv csv = InterviewerCollectCsv.from(data);
        return buildResponse(csv, campaignId + "_Avancement_collecte_enqueteurs", date);
    }
}
