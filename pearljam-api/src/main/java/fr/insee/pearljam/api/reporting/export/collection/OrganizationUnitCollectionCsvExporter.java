package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.api.reporting.presenter.CampaignCollectionByOrganizationUnitsPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByOrganizationUnitsResponse;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByOrganizationUnitsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OrganizationUnitCollectionCsvExporter extends AbstractCsvExporter {

    private final CampaignCollectionByOrganizationUnitsPresenter presenter;
    private final CampaignReportingByOrganizationUnitsPort campaignReportingByOrganizationUnitsPort;

    public ResponseEntity<byte[]> export(String userId, String campaignId, LocalDate date)
            throws CampaignNotFoundException {
        CampaignCollectionByOrganizationUnitsResponse data =
                campaignReportingByOrganizationUnitsPort.getProgressForDay(userId, campaignId, date, presenter);
        OrganizationUnitCollectionCsv csv = OrganizationUnitCollectionCsv.from(data);
        return buildResponse(csv, campaignId + "_Avancement_collecte_sites", date);
    }
}
