package fr.insee.pearljam.api.reporting.export.collect;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionResponse;

import java.util.ArrayList;
import java.util.List;

public record CampaignCollectionCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<CollectCsvHeaders> CSV_HEADERS = CollectCsvHeaders.buildHeaders(
            List.of(CollectCsvHeaders.CAMPAIGN_LABEL)
    );

    public static CampaignCollectionCsv from(List<CampaignCollectionResponse> responses) {
        List<CsvRow> rows = responses.stream()
                .map(response -> {
                    List<Object> values = new ArrayList<>();
                    values.add(response.campaignLabel());
                    values.addAll(CollectCsvRow.commonValues(
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
                        .map(CollectCsvHeaders::getHeaderName)
                        .toArray());
    }
}
