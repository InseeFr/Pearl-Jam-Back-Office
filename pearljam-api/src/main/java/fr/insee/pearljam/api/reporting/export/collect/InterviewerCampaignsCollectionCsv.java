package fr.insee.pearljam.api.reporting.export.collect;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignCollectionResponse;

import java.util.ArrayList;
import java.util.List;

public record InterviewerCampaignsCollectionCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<CollectCsvHeaders> CSV_HEADERS = CollectCsvHeaders.buildHeaders(
            List.of(CollectCsvHeaders.CAMPAIGN_LABEL)
    );

    public static InterviewerCampaignsCollectionCsv from(List<InterviewerCampaignCollectionResponse> responses) {
        List<CsvRow> rows = responses.stream()
                .map(response -> {
                    List<Object> values = new ArrayList<>();
                    values.add(response.campaignLabel());
                    values.addAll(CollectCsvRow.commonValues(
                            response.rates(), response.outcomes(), response.closingCauses(),
                            response.allocatedInterviewers()));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new InterviewerCampaignsCollectionCsv(rows);
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
