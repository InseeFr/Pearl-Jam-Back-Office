package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InterviewerCampaignsProgressCsvPresenter
        implements InterviewerCampaignsStatsPresenter<InterviewerCampaignsProgressCsv> {

    @Override
    public InterviewerCampaignsProgressCsv present(List<InterviewerCampaignDailyStats> stats) {
        List<CsvRow> rows = stats.stream()
                .map(campaignStats -> {
                    List<Object> values = new ArrayList<>();
                    values.add(campaignStats.getCampaignLabel());
                    values.addAll(ProgressCsvRow.commonValues(campaignStats));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new InterviewerCampaignsProgressCsv(rows);
    }
}
