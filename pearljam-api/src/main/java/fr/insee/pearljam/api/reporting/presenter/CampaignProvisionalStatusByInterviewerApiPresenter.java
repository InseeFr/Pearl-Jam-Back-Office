package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignProvisionalStatusByInterviewersResponse.Interviewer;
import fr.insee.pearljam.api.reporting.response.CampaignProvisionalStatusByInterviewersResponse.Interviewer.SurveyUnitsResponse;
import fr.insee.pearljam.api.reporting.response.CampaignProvisionalStatusByInterviewersResponse.OrganizationUnitSite;
import fr.insee.pearljam.api.reporting.response.CampaignProvisionalStatusByInterviewersResponse.OrganizationUnitSite.SurveyUnitsSiteResponse;
import fr.insee.pearljam.api.reporting.response.CampaignProvisionalStatusByInterviewersResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignProvisionalStatusByInterviewerApiPresenter implements CampaignStatsByInterviewersPresenter<CampaignProvisionalStatusByInterviewersResponse> {
    @Override
    public CampaignProvisionalStatusByInterviewersResponse present(List<InterviewerDailyStats> interviewerStats, CampaignDailyStats siteStats, CampaignDailyStats campaignStats) {

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

        OrganizationUnitSite organizationUnitSite;
        organizationUnitSite = new OrganizationUnitSite(
                new SurveyUnitsSiteResponse(
                        campaignStats.getAllocatedCount(),
                        new SurveyUnitsSiteResponse.ClosingCauseSiteResponse(
                                campaignStats.getNpaClosingCauseCount(),
                                campaignStats.getNpiClosingCauseCount(),
                                campaignStats.getNpxClosingCauseCount(),
                                campaignStats.getRowClosingCauseCount(),
                                siteStats.getTotalClosingCauses()
                        ))
        );

        return new CampaignProvisionalStatusByInterviewersResponse(interviewers, organizationUnitSite);
    }
}
