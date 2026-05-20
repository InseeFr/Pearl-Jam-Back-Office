package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.pearljam.api.reporting.export.csv.CsvRow.addRowWithLabel;

@Component
public class CampaignCollectionCsvPresenter implements CampaignStatsPresenter<CampaignCollectionCsv> {

    @Override
    public CampaignCollectionCsv present(List<CampaignDailyStats> stats) {

        List<CsvRow> rows = new ArrayList<>();
        stats.forEach(campaignStats ->
                addRowWithLabel(rows, campaignStats.getCampaignLabel(), CollectionCsvRow.commonValues(campaignStats)));

        return new CampaignCollectionCsv(rows);
    }
}
