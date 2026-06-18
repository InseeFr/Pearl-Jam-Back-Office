package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.Interviewer;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.Interviewer.SurveyUnitsResponse;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.TotalInterviewers;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.TotalInterviewers.TotalInterviewerClosingCauses;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignClosingCausesByInterviewerPresenter implements CampaignStatsByInterviewersPresenter<CampaignClosingCausesByInterviewersResponse> {
    @Override
    public CampaignClosingCausesByInterviewersResponse present(List<InterviewerDailyStats> interviewerStats, CampaignDailyStats siteStats, CampaignDailyStats campaignStats) {

        List<Interviewer> interviewers;
        interviewers = interviewerStats.stream().map(interviewerDailyStats ->
                new Interviewer(
                        interviewerDailyStats.getInterviewerId(),
                        interviewerDailyStats.getInterviewerFirstName() + " " + interviewerDailyStats.getInterviewerLastName(),

                        new SurveyUnitsResponse(
                                interviewerDailyStats.getAllocatedCount(),
                        new SurveyUnitsResponse.ClosingCauseResponse(
                                interviewerDailyStats.getNpaProvisionalClosingCauseCount(),
                                interviewerDailyStats.getNpiProvisionalClosingCauseCount(),
                                interviewerDailyStats.getNpxProvisionalClosingCauseCount(),
                                interviewerDailyStats.getRowProvisionalClosingCauseCount(),
                                interviewerDailyStats.getTotalProvisionalClosingCauses()
                        ))
                )).toList();

        long totalSUInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getAllocatedCount).sum();
        long totalNpaInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getNpaProvisionalClosingCauseCount).sum();
        long totalNpiInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getNpiProvisionalClosingCauseCount).sum();
        long totalNpxInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getNpxProvisionalClosingCauseCount).sum();
        long totalRowInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getRowProvisionalClosingCauseCount).sum();
        long totalClosingCauseInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getTotalProvisionalClosingCauses).sum();

        TotalInterviewers totalInterviewers = new TotalInterviewers(
                        totalSUInterviewers,
                        new TotalInterviewerClosingCauses(
                                totalNpaInterviewers,
                                totalNpiInterviewers,
                                totalNpxInterviewers,
                                totalRowInterviewers,
                                totalClosingCauseInterviewers
                        )
        );

        return new CampaignClosingCausesByInterviewersResponse(interviewers, totalInterviewers);
    }
}
