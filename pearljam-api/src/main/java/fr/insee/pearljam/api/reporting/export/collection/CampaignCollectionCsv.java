package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;

import java.util.ArrayList;
import java.util.List;

public record CampaignCollectionCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<CollectionCsvHeaders> CSV_HEADERS = CollectionCsvHeaders.buildHeaders(
            List.of(CollectionCsvHeaders.CAMPAIGN_LABEL)
    );

    public static CampaignCollectionCsv from(List<CampaignCollectionResponse> responses) {
        List<CsvRow> rows = responses.stream()
                .map(response -> {
                    List<Object> values = new ArrayList<>();
                    values.add(response.campaignLabel());
                    values.addAll(CollectionCsvRow.commonValues(
                            response.rates(), response.outcomes(), response.closingCauses(),
                            response.allocated()));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new CampaignCollectionCsv(rows);
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
