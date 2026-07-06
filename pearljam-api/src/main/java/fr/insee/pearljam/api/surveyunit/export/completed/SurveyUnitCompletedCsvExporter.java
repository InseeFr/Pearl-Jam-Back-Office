package fr.insee.pearljam.api.surveyunit.export.completed;


import fr.insee.pearljam.api.export.csv.AbstractCsvExporter;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitCompletedPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class SurveyUnitCompletedCsvExporter extends AbstractCsvExporter {

    private final SurveyUnitCompletedCsvPresenter presenter;
    private final SurveyUnitCompletedPort surveyUnitCompletedPort;

    public ResponseEntity<byte[]> export(String userId, String campaignId) {
        SurveyUnitCompletedCsv csv =
                surveyUnitCompletedPort.getCompletedSurveyUnits(userId, campaignId, "", Pageable.unpaged(), presenter);
        return buildResponse(csv, campaignId + "_Unites_terminees", LocalDate.now(ZoneId.of("UTC")));
    }
}
