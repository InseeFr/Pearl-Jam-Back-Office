package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InterviewerProgressCsvPresenter
        implements CampaignStatsByInterviewersPresenter<InterviewerProgressCsv> {

    @Override
    public InterviewerProgressCsv present(List<InterviewerDailyStats> interviewerStats,
                                          CampaignDailyStats siteStats,
                                          CampaignDailyStats campaignStats) {
        List<CsvRow> rows = interviewerStats.stream()
                .map(interviewer -> {
                    List<Object> values = new ArrayList<>();
                    values.add(interviewer.getInterviewerFirstName() + " " + interviewer.getInterviewerLastName());
                    values.add(interviewer.getInterviewerId());
                    values.addAll(ProgressCsvRow.commonValues(interviewer));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new InterviewerProgressCsv(rows);
    }
}
