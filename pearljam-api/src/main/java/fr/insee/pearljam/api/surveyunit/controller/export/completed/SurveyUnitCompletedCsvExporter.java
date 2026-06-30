package fr.insee.pearljam.api.surveyunit.controller.export.completed;


import fr.insee.pearljam.api.reporting.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.surveyunit.port.in.application.SurveyUnitCompletedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SurveyUnitCompletedCsvExporter extends AbstractCsvExporter {

    private final SurveyUnitCompletedCsvPresenter presenter;
    private final SurveyUnitCompletedPort surveyUnitCompletedPort;

    public ResponseEntity<byte[]> export(String userId, String campaignId) {
        SurveyUnitCompletedCsv csv =
                surveyUnitCompletedPort.getCompletedSurveyUnits(userId, campaignId, "", Pageable.unpaged(), presenter);
        return buildResponse(csv, campaignId + "_Unites_terminees", LocalDate.now());
    }
}
