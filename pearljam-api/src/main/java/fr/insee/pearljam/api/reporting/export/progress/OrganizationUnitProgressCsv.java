package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.CampaignProgressByOrganizationUnitsResponse;

import java.util.ArrayList;
import java.util.List;

public record OrganizationUnitProgressCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<ProgressCsvHeaders> CSV_HEADERS = ProgressCsvHeaders.buildHeaders(
            List.of(ProgressCsvHeaders.ORGANIZATION_UNIT_LABEL)
    );

    public static OrganizationUnitProgressCsv from(CampaignProgressByOrganizationUnitsResponse response) {
        List<CsvRow> rows = response.organizationUnits().stream()
                .map(organizationUnit -> {
                    List<Object> values = new ArrayList<>();
                    values.add(organizationUnit.organizationUnitLabel());
                    values.addAll(ProgressCsvRow.commonValues(
                            organizationUnit.progressRate(), organizationUnit.states(), organizationUnit.communications()));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new OrganizationUnitProgressCsv(rows);
    }

    @Override
    public CsvRow headers() {
        return CsvRow.from(
                CSV_HEADERS
                        .stream()
                        .map(ProgressCsvHeaders::getHeaderName)
                        .toArray());
    }
}
