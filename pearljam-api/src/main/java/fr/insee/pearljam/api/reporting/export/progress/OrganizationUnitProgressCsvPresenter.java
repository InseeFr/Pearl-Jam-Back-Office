package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByOrganizationUnitsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.OrganizationUnitDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrganizationUnitProgressCsvPresenter
        implements CampaignStatsByOrganizationUnitsPresenter<OrganizationUnitProgressCsv> {

    @Override
    public OrganizationUnitProgressCsv present(List<OrganizationUnitDailyStats> organizationUnitStats,
                                               CampaignDailyStats campaignStats) {
        List<CsvRow> rows = organizationUnitStats.stream()
                .map(ou -> {
                    List<Object> values = new ArrayList<>();
                    values.add(ou.getOuLabel());
                    values.addAll(ProgressCsvRow.commonValues(ou));
                    return CsvRow.from(values.toArray());
                })
                .toList();
        return new OrganizationUnitProgressCsv(rows);
    }
}
