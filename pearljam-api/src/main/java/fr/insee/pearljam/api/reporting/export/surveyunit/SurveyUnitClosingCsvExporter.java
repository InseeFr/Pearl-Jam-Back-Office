package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SurveyUnitClosingCsvExporter extends AbstractCsvExporter {

    private final SurveyUnitClosingApiCsvPresenter presenter;
    private final SurveyUnitClosingPort surveyUnitClosingPort;


    public ResponseEntity<byte[]> export(String userId) {
        SurveyUnitClosingCsv csv = surveyUnitClosingPort.getSurveyUnitsToClose(userId, presenter);
        return buildResponse(csv, "XX" + "UE_à_clore" + "YY", LocalDate.now());
    }
}



//public class OrganizationUnitCollectionCsvExporter extends AbstractCsvExporter {
//
//    private final OrganizationUnitCollectionCsvPresenter presenter;
//    private final CampaignReportingByOrganizationUnitsPort campaignReportingByOrganizationUnitsPort;
//
//    public ResponseEntity<byte[]> export(String userId, String campaignId, LocalDate date)
//            throws CampaignNotFoundException {
//        OrganizationUnitCollectionCsv csv =
//                campaignReportingByOrganizationUnitsPort.getProgressForDay(userId, campaignId, date, presenter);
//        return buildResponse(csv, campaignId + "_Avancement_collecte_sites", date);
//    }
//}
