package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProvisionalStatusByInterviewersResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;

import java.util.List;

public class CampaignProvisionalStatusByInterviewerApiPresenter implements CampaignStatsByInterviewersPresenter<List<CampaignProvisionalStatusByInterviewersResponse>> {
    @Override
    public List<CampaignProvisionalStatusByInterviewersResponse> present(List<InterviewerDailyStats> interviewerStats, CampaignDailyStats siteStats, CampaignDailyStats campaignStats) {
        return List.of();
    }
}
