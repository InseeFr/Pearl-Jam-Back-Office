package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;

import java.util.List;

public record InterviewerCollectionCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<CollectionCsvHeaders> CSV_HEADERS = CollectionCsvHeaders.buildHeadersWithSuffix(
            List.of(CollectionCsvHeaders.INTERVIEWER_LABEL, CollectionCsvHeaders.INTERVIEWER_ID),
            List.of(CollectionCsvHeaders.ALLOCATED_INTERVIEWERS)
    );

    @Override
    public CsvRow headers() {
        return CsvRow.from(
                CSV_HEADERS
                        .stream()
                        .map(CollectionCsvHeaders::getHeaderName)
                        .toArray());
    }
}
