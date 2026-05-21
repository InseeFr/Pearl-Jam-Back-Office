package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.pearljam.api.reporting.export.csv.CsvRow.*;

@Component
public class InterviewerProgressCsvPresenter
        implements CampaignStatsByInterviewersPresenter<InterviewerProgressCsv> {

    @Override
    public InterviewerProgressCsv present(List<InterviewerDailyStats> interviewerStats,
                                          CampaignDailyStats siteStats,
                                          CampaignDailyStats campaignStats) {
        List<CsvRow> rows = new ArrayList<>();
        interviewerStats.forEach(interv ->
                addRowWithMultipleTitleLabel(
                        rows,
                        List.of(interv.getInterviewerFirstName() + " " + interv.getInterviewerLastName(), interv.getInterviewerId()),
                        ProgressCsvRow.commonValues(interv)));

        addRowWithTitleLabel(rows, ProgressCsvRow.TOTAL_UNAFFECTED,
                // 1 Column for Total France
                // followed by 1 Column for Idep + Common values columns with emptyRowWithValueAtSpecificPosition
                emptyRowWithValueAtSpecificPosition(campaignStats.getUnaffectedCount(), 2, ProgressCsvRow.commonValuesSize() + 1));
        addRowWithTitleLabel(rows, ProgressCsvRow.TOTAL_FRANCE, ProgressCsvRow.commonValues((campaignStats)));
        addRowWithTitleLabel(rows, ProgressCsvRow.TOTAL_SITE, ProgressCsvRow.commonValues((siteStats)));

        return new InterviewerProgressCsv(rows);
    }
}
