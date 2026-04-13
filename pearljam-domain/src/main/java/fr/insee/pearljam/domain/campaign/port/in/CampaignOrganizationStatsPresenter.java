package fr.insee.pearljam.domain.campaign.port.in;

import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;

import java.util.List;

public interface CampaignOrganizationStatsPresenter<T> {
        T present(CampaignDailyStats campaignDailyStats,
                  CampaignVisibility campaignVisibility,
                  List<Referent> referents,
                  List<InterviewerDailyStats> interviewerDailyStats,
                  long currentDate);
}
