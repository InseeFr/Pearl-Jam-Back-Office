package fr.insee.pearljam.api.reporting.presenter;


import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse;
import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterviewerCampaignsClosingCausesPresenter implements InterviewerCampaignsStatsPresenter<List<InterviewerCampaignsClosingCausesResponse>> {

    @Override
    public List<InterviewerCampaignsClosingCausesResponse> present(List<InterviewerCampaignDailyStats> stats) {
        return stats.stream().map(interv ->
                new InterviewerCampaignsClosingCausesResponse(
                        interv.getCampaignId(),
                        interv.getAllocatedCount(),
                        new InterviewerCampaignsClosingCausesResponse.ClosingCauseResponse(
                            interv.getNpaClosingCauseCount(),
                            interv.getNpiClosingCauseCount(),
                            interv.getNpxClosingCauseCount(),
                            interv.getRowClosingCauseCount(),
                            interv.getTotalClosingCauses())
                        )
        ).toList();
    }
}