package fr.insee.pearljam.api.reporting.export.collect;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByOrganizationUnitsResponse;

import java.util.ArrayList;
import java.util.List;

public record OrganizationUnitCollectCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<CollectCsvHeaders> CSV_HEADERS = CollectCsvHeaders.buildHeaders(
            List.of(CollectCsvHeaders.ORGANIZATION_UNIT_LABEL)
    );

    public static OrganizationUnitCollectCsv from(CampaignCollectionByOrganizationUnitsResponse response) {
        List<CsvRow> rows = response.organizationUnits().stream()
                .map(organizationUnit -> {
                    List<Object> values = new ArrayList<>();
                    values.add(organizationUnit.organizationUnitLabel());
                    values.addAll(CollectCsvRow.commonValues(
                            organizationUnit.rates(), organizationUnit.outcomes(), organizationUnit.closingCauses(),
                            organizationUnit.allocated()));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new OrganizationUnitCollectCsv(rows);
    }

    @Override
    public CsvRow headers() {
        return CsvRow.from(
                CSV_HEADERS
                        .stream()
                        .map(CollectCsvHeaders::getHeaderName)
                        .toArray());
    }
}
