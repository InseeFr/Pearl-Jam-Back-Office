package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsProgressResponse;

import java.util.ArrayList;
import java.util.List;

public record InterviewerCampaignsProgressCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<ProgressCsvHeaders> CSV_HEADERS = ProgressCsvHeaders.buildHeaders(
            List.of(ProgressCsvHeaders.CAMPAIGN_LABEL)
    );

    public static InterviewerCampaignsProgressCsv from(List<InterviewerCampaignsProgressResponse> responses) {
        List<CsvRow> rows = responses.stream()
                .map(response -> {
                    List<Object> values = new ArrayList<>();
                    values.add(response.campaignLabel());
                    values.addAll(ProgressCsvRow.commonValues(
                            response.progressRate(), response.states(), response.communications()));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new InterviewerCampaignsProgressCsv(rows);
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
