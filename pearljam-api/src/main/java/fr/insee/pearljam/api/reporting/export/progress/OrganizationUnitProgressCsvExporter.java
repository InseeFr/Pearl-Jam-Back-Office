package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.api.reporting.presenter.CampaignProgressByOrganizationUnitsPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressByOrganizationUnitsResponse;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByOrganizationUnitsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OrganizationUnitProgressCsvExporter extends AbstractCsvExporter {

    private final CampaignProgressByOrganizationUnitsPresenter presenter;
    private final CampaignReportingByOrganizationUnitsPort campaignReportingByOrganizationUnitsPort;

    public ResponseEntity<byte[]> export(String userId, String campaignId, LocalDate date)
            throws CampaignNotFoundException {
        CampaignProgressByOrganizationUnitsResponse data =
                campaignReportingByOrganizationUnitsPort.getProgressForDay(userId, campaignId, date, presenter);
        OrganizationUnitProgressCsv csv = OrganizationUnitProgressCsv.from(data);
        return buildResponse(csv, campaignId + "_Avancement_sites", date);
    }
}
