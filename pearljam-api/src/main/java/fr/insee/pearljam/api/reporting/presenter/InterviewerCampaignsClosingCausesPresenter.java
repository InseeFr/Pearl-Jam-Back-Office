package fr.insee.pearljam.api.reporting.presenter;


import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse;
import fr.insee.pearljam.api.reporting.response.InterviewerCampaignsClosingCausesResponse.*;

import fr.insee.pearljam.domain.reporting.port.in.InterviewerCampaignsStatsPresenter;
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
                        interv.getCampaignId(),
                        interv.getAllocatedCount(),
                        new InterviewerCampaignSurveyUnits.ClosingCauseResponse(
                            interv.getNpaClosingCauseCount(),
                            interv.getNpiClosingCauseCount(),
                            interv.getNpxClosingCauseCount(),
                            interv.getRowClosingCauseCount(),
                            interv.getTotalClosingCauses())
                        )
        ).toList();

        long totalSUInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getAllocatedCount).sum();
        long totalNpaInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getNpaClosingCauseCount).sum();
        long totalNpiInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getNpiClosingCauseCount).sum();
        long totalNpxInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getNpxClosingCauseCount).sum();
        long totalRowInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getRowClosingCauseCount).sum();
        long totalClosingCauseInterviewer = stats.stream().mapToLong(InterviewerCampaignDailyStats::getTotalClosingCauses).sum();


        InterviewerCampaignsTotalSurveyUnit interviewerCampaignsTotalSurveyUnit = new InterviewerCampaignsTotalSurveyUnit(
                totalSUInterviewer,
                new InterviewerCampaignsTotalSurveyUnit.ClosingCauseResponse(
                        totalNpaInterviewer,
                        totalNpiInterviewer,
                        totalNpxInterviewer,
                        totalRowInterviewer,
                        totalClosingCauseInterviewer
                ));

        return new InterviewerCampaignsClosingCausesResponse(interviewerCampaignSurveyUnits, interviewerCampaignsTotalSurveyUnit);
    }
}