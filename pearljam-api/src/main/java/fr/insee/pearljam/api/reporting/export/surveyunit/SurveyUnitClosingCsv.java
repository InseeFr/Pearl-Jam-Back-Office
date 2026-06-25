package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;

import java.util.List;

public record SurveyUnitClosingCsv() implements CsvExportable {
    @Override
    public CsvRow headers() {
        return null;
    }

    @Override
    public List<CsvRow> rows() {
        return List.of();
    }
}
