package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.pearljam.api.export.csv.CsvRow.addRowWithTitleLabel;

@Component
public class InterviewerCampaignsProgressCsvPresenter
        implements InterviewerCampaignsStatsPresenter<InterviewerCampaignsProgressCsv> {

    @Override
    public InterviewerCampaignsProgressCsv present(List<InterviewerCampaignDailyStats> stats) {
        List<CsvRow> rows = new ArrayList<>();
        stats.forEach(campaignStats ->
                addRowWithTitleLabel(rows, campaignStats.getCampaignLabel(), ProgressCsvRow.commonValues(campaignStats)));

        return new InterviewerCampaignsProgressCsv(rows);
    }
}
