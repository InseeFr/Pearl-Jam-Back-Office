package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByOrganizationUnitsResponse;

import java.util.ArrayList;
import java.util.List;

public record OrganizationUnitCollectionCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<CollectionCsvHeaders> CSV_HEADERS = CollectionCsvHeaders.buildHeaders(
            List.of(CollectionCsvHeaders.ORGANIZATION_UNIT_LABEL)
    );

    public static OrganizationUnitCollectionCsv from(CampaignCollectionByOrganizationUnitsResponse response) {
        List<CsvRow> rows = response.organizationUnits().stream()
                .map(organizationUnit -> {
                    List<Object> values = new ArrayList<>();
                    values.add(organizationUnit.organizationUnitLabel());
                    values.addAll(CollectionCsvRow.commonValues(
                            organizationUnit.rates(), organizationUnit.outcomes(), organizationUnit.closingCauses(),
                            organizationUnit.allocated()));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new OrganizationUnitCollectionCsv(rows);
    }

    @Override
    public CsvRow headers() {
        return CsvRow.from(
                CSV_HEADERS
                        .stream()
                        .map(CollectionCsvHeaders::getHeaderName)
                        .toArray());
    }
}
