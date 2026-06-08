package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.Interviewer;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.Interviewer.SurveyUnitsResponse;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.OrganizationUnitSite;
import fr.insee.pearljam.api.reporting.response.CampaignClosingCausesByInterviewersResponse.OrganizationUnitSite.SurveyUnitsSiteResponse;
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
                                interviewerDailyStats.getNpaClosingCauseCount(),
                                interviewerDailyStats.getNpiClosingCauseCount(),
                                interviewerDailyStats.getNpxClosingCauseCount(),
                                interviewerDailyStats.getRowClosingCauseCount(),
                                interviewerDailyStats.getTotalClosingCauses()
                        ))
                )).toList();

        long totalSUInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getAllocatedCount).sum();
        long totalNpaInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getNpaClosingCauseCount).sum();
        long totalNpiInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getNpiClosingCauseCount).sum();
        long totalNpxInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getNpxClosingCauseCount).sum();
        long totalRowInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getRowClosingCauseCount).sum();
        long totalClosingCauseInterviewers = interviewerStats.stream().mapToLong(InterviewerDailyStats::getTotalClosingCauses).sum();

        OrganizationUnitSite organizationUnitSite;
        organizationUnitSite = new OrganizationUnitSite(
                new SurveyUnitsSiteResponse(
                        totalSUInterviewers,
                        new SurveyUnitsSiteResponse.ClosingCauseSiteResponse(
                                totalNpaInterviewers,
                                totalNpiInterviewers,
                                totalNpxInterviewers,
                                totalRowInterviewers,
                                totalClosingCauseInterviewers
                        ))
        );

        return new CampaignClosingCausesByInterviewersResponse(interviewers, organizationUnitSite);
    }
}
