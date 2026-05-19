package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByOrganizationUnitsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fr.insee.pearljam.api.reporting.export.collection.CollectionCsvRow.TOTAL_FRANCE;
import static fr.insee.pearljam.api.reporting.export.collection.OrganizationUnitCollectionCsv.TOTAL_FRANCE;

@Component
public class OrganizationUnitCollectionCsvPresenter
        implements CampaignStatsByOrganizationUnitsPresenter<OrganizationUnitCollectionCsv> {

    @Override
    public OrganizationUnitCollectionCsv present(List<OrganizationUnitDailyStats> organizationUnitStats,
                                                 CampaignDailyStats campaignStats) {
        List<CsvRow> rows = new ArrayList<>();

        organizationUnitStats.forEach(ou -> {
                    List<Object> values = new ArrayList<>();
                    values.add(ou.getOuLabel());
                    values.addAll(CollectionCsvRow.commonValues(ou));
                    values.add(ou.getAllocatedCount());
                    rows.add((CsvRow.from(values.toArray())));
                });

        List<String> list = new ArrayList<>(Collections.nCopies(CollectionCsvRow.commonValuesSize() - 2, ""));
        List<Object> rowData = new ArrayList<>();
        rowData.add(TOTAL_FRANCE);
        rowData.addAll(list);
        rowData.add(campaignStats.getAllocatedCount());
        rows.add(CsvRow.from(rowData.toArray()));

        return new OrganizationUnitCollectionCsv(rows);
    }
}
