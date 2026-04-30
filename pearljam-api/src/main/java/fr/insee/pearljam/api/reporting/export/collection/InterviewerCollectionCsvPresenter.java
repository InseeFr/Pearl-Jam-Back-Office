package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InterviewerCollectionCsvPresenter
        implements CampaignStatsByInterviewersPresenter<InterviewerCollectionCsv> {

    @Override
    public InterviewerCollectionCsv present(List<InterviewerDailyStats> interviewerStats,
                                            CampaignDailyStats siteStats,
                                            CampaignDailyStats campaignStats) {
        List<CsvRow> rows = interviewerStats.stream()
                .map(interviewer -> {
                    List<Object> values = new ArrayList<>();
                    values.add(interviewer.getInterviewerFirstName() + " " + interviewer.getInterviewerLastName());
                    values.add(interviewer.getInterviewerId());
                    values.addAll(CollectionCsvRow.commonValues(interviewer));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new InterviewerCollectionCsv(rows);
    }
}
