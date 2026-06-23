package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.pearljam.api.reporting.export.collection.CollectionCsvRow.*;
import static fr.insee.pearljam.api.reporting.export.csv.CsvRow.*;

@Component
public class InterviewerCollectionCsvPresenter
        implements CampaignStatsByInterviewersPresenter<InterviewerCollectionCsv> {

    @Override
    public InterviewerCollectionCsv present(List<InterviewerDailyStats> interviewerStats,
                                            CampaignDailyStats siteStats,
                                            CampaignDailyStats campaignStats) {

        List<CsvRow> rows = new ArrayList<>();
        interviewerStats.forEach(interv ->
                addRowWithMultipleTitleLabel(rows,
                        List.of(interv.getInterviewerFirstName() + " " + interv.getInterviewerLastName(), interv.getInterviewerId()),
                        CollectionCsvRow.commonValues(interv)));

        addRowWithTitleLabel(rows, TOTAL_UNAFFECTED,
                // 1 Column for TOTAL_UNAFFECTED
                // followed by 1 Column for Idep + Common values columns with emptyRowWithValueAtSpecificPosition
                emptyRowWithValueAtSpecificPosition(campaignStats.getUnaffectedCount(),
                        CollectionCsvRow.commonValuesSize(), CollectionCsvRow.commonValuesSize() + 1));
        addRowWithTitleLabel(rows, TOTAL_SITE, CollectionCsvRow.commonValuesWithEmptyIdep((siteStats)));
        addRowWithTitleLabel(rows, TOTAL_FRANCE, CollectionCsvRow.commonValuesWithEmptyIdep((campaignStats)));

        return new InterviewerCollectionCsv(rows);
    }
}
