package fr.insee.pearljam.api.reporting.export.collect;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByInterviewersResponse;

import java.util.ArrayList;
import java.util.List;

public record InterviewerCollectCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<CollectCsvHeaders> CSV_HEADERS = CollectCsvHeaders.buildHeaders(
            List.of(CollectCsvHeaders.INTERVIEWER_LABEL, CollectCsvHeaders.INTERVIEWER_ID)
    );

    public static InterviewerCollectCsv from(CampaignCollectionByInterviewersResponse response) {
        List<CsvRow> rows = response.interviewers().stream()
                .map(interviewer -> {
                    List<Object> values = new ArrayList<>();
                    values.add(interviewer.interviewerLabel());
                    values.add(interviewer.interviewerId());
                    values.addAll(CollectCsvRow.commonValues(
                            interviewer.rates(), interviewer.outcomes(), interviewer.closingCauses(),
                            interviewer.allocated()));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new InterviewerCollectCsv(rows);
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
