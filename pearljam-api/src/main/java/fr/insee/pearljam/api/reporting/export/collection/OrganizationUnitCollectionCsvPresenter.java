package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByOrganizationUnitsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.pearljam.api.reporting.export.csv.CsvRow.addRowWithLabel;

@Component
public class OrganizationUnitCollectionCsvPresenter
        implements CampaignStatsByOrganizationUnitsPresenter<OrganizationUnitCollectionCsv> {

    @Override
    public OrganizationUnitCollectionCsv present(List<OrganizationUnitDailyStats> organizationUnitStats,
                                                 CampaignDailyStats campaignStats) {

        List<CsvRow> rows = new ArrayList<>();
        organizationUnitStats.forEach(ou ->
                addRowWithLabel(rows, ou.getOuLabel(), CollectionCsvRow.commonValues(ou)));

        addRowWithLabel(rows, CollectionCsvRow.TOTAL_FRANCE, CollectionCsvRow.commonValues(campaignStats));

        return new OrganizationUnitCollectionCsv(rows);
    }
}
