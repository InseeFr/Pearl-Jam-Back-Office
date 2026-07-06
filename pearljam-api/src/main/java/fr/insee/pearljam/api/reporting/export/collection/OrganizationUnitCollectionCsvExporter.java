package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByOrganizationUnitsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class OrganizationUnitCollectionCsvExporter extends AbstractCsvExporter {

    private final OrganizationUnitCollectionCsvPresenter presenter;
    private final CampaignReportingByOrganizationUnitsPort campaignReportingByOrganizationUnitsPort;

    public ResponseEntity<byte[]> export(String userId, String campaignId, LocalDate date)
            throws CampaignNotFoundException {
        OrganizationUnitCollectionCsv csv =
                campaignReportingByOrganizationUnitsPort.getProgressForDay(userId, campaignId, date, presenter);
        return buildResponse(csv, campaignId + "_Avancement_collecte_sites", date);
    }
}
