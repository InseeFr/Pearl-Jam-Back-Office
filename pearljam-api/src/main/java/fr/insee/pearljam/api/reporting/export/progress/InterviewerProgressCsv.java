package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;

import java.util.List;

public record InterviewerProgressCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<ProgressCsvHeaders> CSV_HEADERS = ProgressCsvHeaders.buildHeaders(
            List.of(ProgressCsvHeaders.INTERVIEWER_LABEL, ProgressCsvHeaders.INTERVIEWER_ID)
    );

    @Override
    public CsvRow headers() {
        return CsvRow.from(
                CSV_HEADERS
                        .stream()
                        .map(ProgressCsvHeaders::getHeaderName)
                        .toArray());
    }
}
