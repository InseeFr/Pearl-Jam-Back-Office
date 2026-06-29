package fr.insee.pearljam.api.surveyunit.export;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.api.surveyunit.csv.SurveyUnitAssignedCsv;
import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitAssignedCsvPresenter;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitAssignedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SurveyUnitAssignedCsvExporter extends AbstractCsvExporter {

    private final SurveyUnitAssignedCsvPresenter surveyUnitAssignedCsvPresenter;
    private final SurveyUnitAssignedPort surveyUnitAssignedPort;

    public ResponseEntity<byte[]> export(String userId, String campaignId, String search) {
        SurveyUnitAssignedCsv csv = surveyUnitAssignedPort.getSurveyUnitsAssigned(
                userId, campaignId, search, Pageable.unpaged(), surveyUnitAssignedCsvPresenter);
        return buildResponse(csv, campaignId + "_UE_confiees" + searchSuffix(search), LocalDate.now());
    }

    private static String searchSuffix(String search) {
        if (search == null || search.isEmpty())
            return "";
        return "_filtre_" + search.replace(' ', '_');
    }

}
