package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;

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

    @Override
    public List<CsvRow> rows() {
        return List.of();
    }
}