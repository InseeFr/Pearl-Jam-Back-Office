package fr.insee.pearljam.api.surveyunit.export.completed;

import fr.insee.pearljam.api.export.csv.CsvExportable;
import fr.insee.pearljam.api.export.csv.CsvRow;

import java.util.List;

public class SurveyUnitCompletedCsv implements CsvExportable {

    protected static final List<SurveyUnitCompletedCsvHeaders> CSV_HEADERS = SurveyUnitCompletedCsvHeaders.commonHeaders();

    @Override
    public CsvRow headers() {
        return CsvRow.from(
                CSV_HEADERS
                        .stream()
                        .map(SurveyUnitCompletedCsvHeaders::getHeaderName)
                        .toArray());
    }



    @Override
    public List<CsvRow> rows() {
        return List.of();
    }
}
