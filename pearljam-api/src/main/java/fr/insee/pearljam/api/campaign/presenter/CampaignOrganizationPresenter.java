package fr.insee.pearljam.api.campaign.presenter;

import fr.insee.pearljam.api.campaign.response.CampaignOrganizationResponse;
import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationStatsPresenter;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignOrganizationPresenter implements CampaignOrganizationStatsPresenter<CampaignOrganizationResponse> {

    @Override
    public CampaignOrganizationResponse present(CampaignDailyStats campaignDailyStats,
                                                CampaignVisibility campaignVisibility,
                                                List<Referent> referents,
                                                List<InterviewerDailyStats> interviewerDailyStats,
                                                long currentDate) {
                return new CampaignOrganizationResponse(
                        campaignVisibility.id(),
                        campaignVisibility.label(),
                        campaignVisibility.email(),
                        campaignVisibility.managementStartDate(),
                        campaignVisibility.identificationPhaseStartDate(),
                        campaignVisibility.collectionStartDate(),
                        campaignVisibility.collectionEndDate(),
                        campaignVisibility.endDate(),
                        CampaignPhase.fromDates(
                                currentDate,
                                campaignVisibility.managementStartDate(),
                                campaignVisibility.collectionStartDate(),
                                campaignVisibility.collectionEndDate(),
                                campaignVisibility.endDate()),
                        referents.stream()
                                .map(r -> new CampaignOrganizationResponse.Referent(
                                        r.firstName(),
                                        r.lastName(),
                                        r.phoneNumber(),
                                        r.role()))
                                .toList(),
                        interviewerDailyStats.stream()
                                .map(i -> new CampaignOrganizationResponse.Interviewer(
                                        i.getInterviewerId(),
                                        i.getInterviewerFirstName() + " " + i.getInterviewerLastName(),
                                        i.getAllocatedStateCount()))
                                .toList(),
                        new CampaignOrganizationResponse.CampaignOrganizationSurveyUnitCount(
                                campaignDailyStats.getAllocatedStateCount(),
                                campaignDailyStats.getUnaffectedCount()));
    }
}
