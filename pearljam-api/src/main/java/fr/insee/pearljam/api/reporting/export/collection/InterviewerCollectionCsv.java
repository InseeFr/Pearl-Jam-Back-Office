package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.CampaignCollectionByInterviewersResponse;

import java.util.ArrayList;
import java.util.List;

public record InterviewerCollectionCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<CollectionCsvHeaders> CSV_HEADERS = CollectionCsvHeaders.buildHeaders(
            List.of(CollectionCsvHeaders.INTERVIEWER_LABEL, CollectionCsvHeaders.INTERVIEWER_ID)
    );

    public static InterviewerCollectionCsv from(CampaignCollectionByInterviewersResponse response) {
        List<CsvRow> rows = response.interviewers().stream()
                .map(interviewer -> {
                    List<Object> values = new ArrayList<>();
                    values.add(interviewer.interviewerLabel());
                    values.add(interviewer.interviewerId());
                    values.addAll(CollectionCsvRow.commonValues(
                            interviewer.rates(), interviewer.outcomes(), interviewer.closingCauses(),
                            interviewer.allocated()));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new InterviewerCollectionCsv(rows);
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
