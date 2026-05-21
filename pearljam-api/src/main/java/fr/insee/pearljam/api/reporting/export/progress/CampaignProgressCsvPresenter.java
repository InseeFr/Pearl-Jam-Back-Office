package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.pearljam.api.reporting.export.csv.CsvRow.addRowWithTitleLabel;

@Component
public class CampaignProgressCsvPresenter implements CampaignStatsPresenter<CampaignProgressCsv> {

    @Override
    public CampaignProgressCsv present(List<CampaignDailyStats> stats) {
        List<CsvRow> rows = new ArrayList<>();
        stats.forEach(campaignStats ->
                addRowWithTitleLabel(rows, campaignStats.getCampaignLabel(), ProgressCsvRow.commonValues(campaignStats)));

        return new CampaignProgressCsv(rows);
    }
}
