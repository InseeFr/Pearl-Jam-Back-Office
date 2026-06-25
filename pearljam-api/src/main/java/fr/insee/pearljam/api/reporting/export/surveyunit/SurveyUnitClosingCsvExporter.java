package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SurveyUnitClosingCsvExporter extends AbstractCsvExporter {

    private final SurveyUnitClosingApiCsvPresenter presenter;
    private final SurveyUnitClosingPort surveyUnitClosingPort;


    public ResponseEntity<byte[]> export(String userId) {
        SurveyUnitClosingCsv csv = surveyUnitClosingPort.getSurveyUnitsToClose(userId, presenter);
        return buildResponse(csv, "UE_à_clore", LocalDate.now());
    }
}
