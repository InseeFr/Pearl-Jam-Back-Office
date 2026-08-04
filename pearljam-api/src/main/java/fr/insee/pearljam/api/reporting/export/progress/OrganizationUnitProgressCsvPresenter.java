package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByOrganizationUnitsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.pearljam.api.export.csv.CsvRow.addRowWithTitleLabel;

@Component
public class OrganizationUnitProgressCsvPresenter
        implements CampaignStatsByOrganizationUnitsPresenter<OrganizationUnitProgressCsv> {

    @Override
    public OrganizationUnitProgressCsv present(List<OrganizationUnitDailyStats> organizationUnitStats,
                                               CampaignDailyStats campaignStats) {

        List<CsvRow> rows = new ArrayList<>();
        organizationUnitStats.forEach(ou ->
                    addRowWithTitleLabel(rows, ou.getOuLabel(), ProgressCsvRow.commonValues(ou)));

        addRowWithTitleLabel(rows, ProgressCsvRow.TOTAL_FRANCE, ProgressCsvRow.commonValues(campaignStats));
        return new OrganizationUnitProgressCsv(rows);
    }
}
