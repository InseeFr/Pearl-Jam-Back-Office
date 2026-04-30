package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CampaignCollectionCsvPresenter implements CampaignStatsPresenter<CampaignCollectionCsv> {

    @Override
    public CampaignCollectionCsv present(List<CampaignDailyStats> stats) {
        List<CsvRow> rows = stats.stream()
                .map(campaignStats -> {
                    List<Object> values = new ArrayList<>();
                    values.add(campaignStats.getCampaignLabel());
                    values.addAll(CollectionCsvRow.commonValues(campaignStats));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new CampaignCollectionCsv(rows);
    }
}
