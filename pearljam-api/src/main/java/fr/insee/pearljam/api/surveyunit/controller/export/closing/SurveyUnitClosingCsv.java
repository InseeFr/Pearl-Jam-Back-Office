package fr.insee.pearljam.api.surveyunit.controller.export.closing;

import fr.insee.pearljam.api.export.csv.CsvExportable;
import fr.insee.pearljam.api.export.csv.CsvRow;

import java.util.Arrays;
import java.util.List;

public record SurveyUnitClosingCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<SurveyUnitClosingCsvHeaders> CSV_HEADERS = SurveyUnitClosingCsvHeaders.buildHeaders();

    @Override
    public CsvRow headers() {
        return CsvRow.from(
                Arrays.stream(SurveyUnitClosingCsvHeaders.values())
                        .map(SurveyUnitClosingCsvHeaders::getHeaderName)
                        .toArray()
        );
    }
}