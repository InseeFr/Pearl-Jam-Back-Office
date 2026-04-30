package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;

import java.util.List;

public record OrganizationUnitCollectionCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<CollectionCsvHeaders> CSV_HEADERS = CollectionCsvHeaders.buildHeaders(
            List.of(CollectionCsvHeaders.ORGANIZATION_UNIT_LABEL)
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
