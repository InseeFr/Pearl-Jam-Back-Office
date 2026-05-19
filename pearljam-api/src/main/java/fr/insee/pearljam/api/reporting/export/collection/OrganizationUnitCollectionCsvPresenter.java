package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByOrganizationUnitsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fr.insee.pearljam.api.reporting.export.collection.OrganizationUnitCollectionCsv.TOTAL_FRANCE;

@Component
public class OrganizationUnitCollectionCsvPresenter
        implements CampaignStatsByOrganizationUnitsPresenter<OrganizationUnitCollectionCsv> {

    @Override
    public OrganizationUnitCollectionCsv present(List<OrganizationUnitDailyStats> organizationUnitStats,
                                                 CampaignDailyStats campaignStats) {
        List<CsvRow> rows = organizationUnitStats.stream()
                .map(ou -> {
                    List<Object> values = new ArrayList<>();
                    values.add(ou.getOuLabel());
                    values.addAll(CollectionCsvRow.commonValues(ou));
                    values.add(ou.getAllocatedCount());
                    return CsvRow.from(values.toArray());
                })
                .toList();

        List<String> list = new ArrayList<>(Collections.nCopies(rows.size() - 2, ""));

        // crash en debug a ""
        // Fix : on a ici 3 colonne mais il faut prendre en compte le nb de row intermediaires (utiliser common rows)
        rows.add(CsvRow.from(
                TOTAL_FRANCE,
                list,
                campaignStats.getAllocatedCount()
        ));

        return new OrganizationUnitCollectionCsv(rows);
    }
}
