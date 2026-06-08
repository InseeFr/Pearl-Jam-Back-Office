package fr.insee.pearljam.api.reporting.presenter;


import fr.insee.pearljam.api.reporting.response.InterviewerClosingCausesByCampaignResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterviewerClosingCausesByCampaignPresenter implements InterviewerCampaignsStatsPresenter<List<InterviewerClosingCausesByCampaignResponse>> {

    @Override
    public List<InterviewerClosingCausesByCampaignResponse> present(List<InterviewerCampaignDailyStats> stats) {
        return stats.stream().map(interv ->
                new InterviewerClosingCausesByCampaignResponse(
                        interv.getCampaignId(),
                        interv.(),
                        )
        )
    }
}


//@Override
//public InterviewerProvisionalStatusByCampaignResponse present(List<CampaignDailyStats> stats) {
//    return new CampaignProvisionalStatusResponse(
//            new SurveyUnitsSiteResponse(
//                    campaignStats.getAllocatedCount(),
//                    new SurveyUnitsSiteResponse.ClosingCauseSiteResponse(
//                            campaignStats.getNpaClosingCauseCount(),
//                            campaignStats.getNpiClosingCauseCount(),
//                            campaignStats.getNpxClosingCauseCount(),
//                            campaignStats.getRowClosingCauseCount(),
//                            siteStats.getTotalClosingCauses()
//                    ))
//    );

