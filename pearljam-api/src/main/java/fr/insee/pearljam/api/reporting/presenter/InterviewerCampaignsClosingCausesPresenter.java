package fr.insee.pearljam.api.reporting.presenter;


import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse.*;

import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterviewerCampaignsClosingCausesPresenter implements InterviewerCampaignsStatsPresenter<InterviewerCampaignsClosingCausesResponse> {

    @Override
    public InterviewerCampaignsClosingCausesResponse present(List<InterviewerCampaignDailyStats> stats) {
        List<InterviewerCampaignSurveyUnits> interviewerCampaignSurveyUnits =
                stats.stream().map(interv ->
                new InterviewerCampaignSurveyUnits(
                        interv.getCampaignLabel(),
                        interv.getAllocatedCount(),
                        new InterviewerCampaignSurveyUnits.ClosingCauseResponse(
                            interv.getNpaProvisionalClosingCauseCount(),
                            interv.getNpiProvisionalClosingCauseCount(),
                            interv.getNpxProvisionalClosingCauseCount(),
                            interv.getRowProvisionalClosingCauseCount(),
                            interv.getTotalProvisionalClosingCauses())
                        )
        ).toList();

        long totalSUInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getAllocatedCount).sum();
        long totalNpaInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getNpaProvisionalClosingCauseCount).sum();
        long totalNpiInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getNpiProvisionalClosingCauseCount).sum();
        long totalNpxInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getNpxProvisionalClosingCauseCount).sum();
        long totalRowInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getRowProvisionalClosingCauseCount).sum();
        long totalClosingCauseInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getTotalProvisionalClosingCauses).sum();


        InterviewerCampaignsTotalSurveyUnit interviewerCampaignsTotalSurveyUnit = new InterviewerCampaignsTotalSurveyUnit(
                totalSUInterviewer,
                new InterviewerCampaignsTotalSurveyUnit.ClosingCauseResponse(
                        totalNpaInterviewer,
                        totalNpiInterviewer,
                        totalNpxInterviewer,
                        totalRowInterviewer,
                        totalClosingCauseInterviewer
                ));

        long maxUpdatedAt = stats.stream().mapToLong(AbstractDailyStats::getUpdatedAt).max().orElse(0L);
        
        return new InterviewerCampaignsClosingCausesResponse(interviewerCampaignSurveyUnits, interviewerCampaignsTotalSurveyUnit, maxUpdatedAt);
    }
}