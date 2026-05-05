package fr.insee.pearljam.api.reporting.export.campaignorganization;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationStatsPresenter;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CampaignOrganizationCsvPresenter implements CampaignOrganizationStatsPresenter<CampaignOrganizationCsv> {

    @Override
    public CampaignOrganizationCsv present(CampaignDailyStats campaignDailyStats,
                                            CampaignVisibility campaignVisibility,
                                            List<Referent> referents,
                                            List<InterviewerDailyStats> interviewerDailyStats,
                                            long currentDate) {
        List<CsvRow> rows = new ArrayList<>();

        // Add rows for each interviewer
        interviewerDailyStats.forEach(interviewer -> {
            String fullName = interviewer.getInterviewerFirstName() + " " + interviewer.getInterviewerLastName();
            rows.add(CsvRow.from(
                    fullName,
                    interviewer.getInterviewerId(),
                    interviewer.getAllocatedCount()
            ));
        });

        // Add NOT_AFFECTED row
        rows.add(CsvRow.from(
                CampaignOrganizationCsv.NOT_AFFECTED,
                "",
                campaignDailyStats.getUnaffectedCount()
        ));

        // Add TOTAL_SITE row
        rows.add(CsvRow.from(
                CampaignOrganizationCsv.TOTAL_SITE,
                "",
                campaignDailyStats.getAllocatedCount()
        ));

        return new CampaignOrganizationCsv(campaignVisibility.label(), rows);
    }
}
