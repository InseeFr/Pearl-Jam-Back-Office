package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SurveyUnitClosingCsvExporter extends AbstractCsvExporter {

    private final SurveyUnitClosingApiCsvPresenter presenter;
    private final SurveyUnitClosingPort surveyUnitClosingPort;


    public ResponseEntity<byte[]> export(String userId,  @Nullable String campaignId, LocalDate date) {
        SurveyUnitClosingCsv csv = surveyUnitClosingPort.getSurveyUnitsToClose(userId, campaignId, presenter);
        String prefix = campaignId != null ? campaignId : "";
        return buildResponse(csv, prefix +  "TOTAL_UE_à_clore", date);
    }
}
