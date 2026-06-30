package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.pearljam.api.export.csv.CsvRow.addRowWithTitleLabel;

@Component
public class InterviewerCampaignsCollectionCsvPresenter
        implements InterviewerCampaignsStatsPresenter<InterviewerCampaignsCollectionCsv> {

    @Override
    public InterviewerCampaignsCollectionCsv present(List<InterviewerCampaignDailyStats> stats) {
        List<CsvRow> rows = new ArrayList<>();
        stats.forEach(campaignStats ->
                addRowWithTitleLabel(rows, campaignStats.getCampaignLabel(), CollectionCsvRow.commonValues(campaignStats)));

        return new InterviewerCampaignsCollectionCsv(rows);
    }
}
