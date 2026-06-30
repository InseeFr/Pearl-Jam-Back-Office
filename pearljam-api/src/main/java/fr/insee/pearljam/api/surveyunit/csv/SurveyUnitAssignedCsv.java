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
        return CsvRow.from(
                SurveyUnitAssignedCsvHeaders.TECHNICAL_SURVEY_UNIT_ID.headerName(),
                SurveyUnitAssignedCsvHeaders.SURVEY_UNIT_ID.headerName(),
                CollectionCsvHeaders.INTERVIEWER_LABEL.getHeaderName(),
                SurveyUnitAssignedCsvHeaders.SUB_SAMPLE_ID.headerName(),
                SurveyUnitAssignedCsvHeaders.LOCATION.headerName(),
                SurveyUnitAssignedCsvHeaders.CITY.headerName(),
                SurveyUnitAssignedCsvHeaders.SURVEY_UNIT_STATE.headerName(),
                SurveyUnitAssignedCsvHeaders.CLOSING_CAUSE.headerName()
        );
    }

    public static CsvRow toCsv(SurveyUnitAssigned surveyUnitAssigned) {
        return CsvRow.from(
                surveyUnitAssigned.surveyUnitId(),
                surveyUnitAssigned.surveyUnitDisplayName(),
                SurveyUnitAssignedPresenter.buildInterviewerLabel(surveyUnitAssigned),
                surveyUnitAssigned.ssech(),
                surveyUnitAssigned.location(),
                surveyUnitAssigned.city(),
                surveyUnitAssigned.questionnaireState(),
                surveyUnitAssigned.closingCause()
        );
    }

}
