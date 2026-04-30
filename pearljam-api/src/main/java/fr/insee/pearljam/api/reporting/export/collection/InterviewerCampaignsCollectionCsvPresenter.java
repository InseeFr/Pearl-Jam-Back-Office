package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InterviewerCampaignsCollectionCsvPresenter
        implements InterviewerCampaignsStatsPresenter<InterviewerCampaignsCollectionCsv> {

    @Override
    public InterviewerCampaignsCollectionCsv present(List<InterviewerCampaignDailyStats> stats) {
        List<CsvRow> rows = stats.stream()
                .map(campaignStats -> {
                    List<Object> values = new ArrayList<>();
                    values.add(campaignStats.getCampaignLabel());
                    values.addAll(CollectionCsvRow.commonValues(campaignStats));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new InterviewerCampaignsCollectionCsv(rows);
    }
}
