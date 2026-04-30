package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.reporting.response.CampaignProgressByInterviewersResponse;

import java.util.ArrayList;
import java.util.List;

public record InterviewerProgressCsv(List<CsvRow> rows) implements CsvExportable {

    public static final List<ProgressCsvHeaders> CSV_HEADERS = ProgressCsvHeaders.buildHeaders(
            List.of(ProgressCsvHeaders.INTERVIEWER_LABEL, ProgressCsvHeaders.INTERVIEWER_ID)
    );

    public static InterviewerProgressCsv from(CampaignProgressByInterviewersResponse response) {
        List<CsvRow> rows = response.interviewers().stream()
                .map(interviewer -> {
                    List<Object> values = new ArrayList<>();
                    values.add(interviewer.interviewerLabel());
                    values.add(interviewer.interviewerId());
                    values.addAll(ProgressCsvRow.commonValues(
                            interviewer.progressRate(), interviewer.states(), interviewer.communications()));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new InterviewerProgressCsv(rows);
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
