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

import static fr.insee.pearljam.api.reporting.export.csv.CsvRow.addRowWithTitleLabel;

@Component
public class CampaignOrganizationCsvPresenter implements CampaignOrganizationStatsPresenter<CampaignOrganizationCsv> {

    @Override
    public CampaignOrganizationCsv present(CampaignDailyStats campaignDailyStats,
                                            CampaignVisibility campaignVisibility,
                                            List<Referent> referents,
                                            List<InterviewerDailyStats> interviewerDailyStats,
                                            long currentDate) {
        List<CsvRow> rows = new ArrayList<>();

        interviewerDailyStats.forEach(interviewer -> {
            String fullNameRowTitle = interviewer.getInterviewerFirstName() + " " + interviewer.getInterviewerLastName();
            addRowWithTitleLabel(rows, fullNameRowTitle, List.of(interviewer.getInterviewerId(), interviewer.getAllocatedCount()));
        });

        addRowWithTitleLabel(rows, CampaignOrganizationCsv.NOT_AFFECTED, List.of("", campaignDailyStats.getUnaffectedCount()));
        addRowWithTitleLabel(rows, CampaignOrganizationCsv.TOTAL_SITE, List.of("", campaignDailyStats.getAllocatedCount()));

        return new CampaignOrganizationCsv(campaignVisibility.id(), rows);
    }
}
