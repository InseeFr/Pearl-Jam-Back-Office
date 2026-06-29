package fr.insee.pearljam.api.surveyunit.csv;

import fr.insee.pearljam.api.reporting.export.collection.CollectionCsvHeaders;
import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitAssignedPresenter;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;

import java.util.List;

public record SurveyUnitAssignedCsv(List<CsvRow> rows) implements CsvExportable {

    @Override
    public CsvRow headers() {
        return new CsvRow(List.of(
                "Identifiant technique",
                "Identifiant de l'ue",
                CollectionCsvHeaders.INTERVIEWER_LABEL.getHeaderName(),
                "Ssech",
                "Département",
                "Commune",
                "Etat de l'UE",
                "Motif provisoire"
        ));
    }

    public static CsvRow toCsv(SurveyUnitAssigned surveyUnitAssigned) {
        return new CsvRow(List.of(
                surveyUnitAssigned.surveyUnitId(),
                surveyUnitAssigned.surveyUnitDisplayName(),
                SurveyUnitAssignedPresenter.buildInterviewerLabel(surveyUnitAssigned),
                surveyUnitAssigned.ssech(),
                surveyUnitAssigned.location(),
                surveyUnitAssigned.city(),
                surveyUnitAssigned.questionnaireState(),
                surveyUnitAssigned.closingCause()
        ));
    }

}
