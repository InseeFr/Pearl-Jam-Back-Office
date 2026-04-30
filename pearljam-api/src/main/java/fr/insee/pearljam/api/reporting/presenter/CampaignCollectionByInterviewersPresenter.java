package fr.insee.pearljam.api.reporting.presenter;

import fr.insee.pearljam.api.reporting.response.CampaignCollectionByInterviewersResponse;
import fr.insee.pearljam.api.reporting.response.ClosingCausesProgressResponse;
import fr.insee.pearljam.api.reporting.response.CollectionRatesResponse;
import fr.insee.pearljam.api.reporting.response.ContactOutcomesProgressResponse;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignCollectionByInterviewersPresenter implements
        CampaignStatsByInterviewersPresenter<CampaignCollectionByInterviewersResponse> {

    @Override
    public CampaignCollectionByInterviewersResponse present(List<InterviewerDailyStats> interviewerStats,
                                                            CampaignDailyStats siteStats,
                                                            CampaignDailyStats campaignStats) {
        return new CampaignCollectionByInterviewersResponse(
                interviewerStats.stream()
                        .map(interviewer -> new CampaignCollectionByInterviewersResponse.Interviewer(
                                interviewer.getInterviewerFirstName() + " " + interviewer.getInterviewerLastName(),
                                interviewer.getAllocatedCount(),
                                CollectionRatesResponse.from(interviewer),
                                ContactOutcomesProgressResponse.from(interviewer),
                                ClosingCausesProgressResponse.from(interviewer)
                        ))
                        .toList(),
                new CampaignCollectionByInterviewersResponse.OrganizationUnit(
                        siteStats.getAllocatedCount(),
                        CollectionRatesResponse.from(siteStats),
                        ContactOutcomesProgressResponse.from(siteStats),
                        ClosingCausesProgressResponse.from(siteStats)
                ),
                new CampaignCollectionByInterviewersResponse.Campaign(
                        campaignStats.getAllocatedCount(),
                        campaignStats.getUnaffectedCount(),
                        CollectionRatesResponse.from(campaignStats),
                        ContactOutcomesProgressResponse.from(campaignStats),
                        ClosingCausesProgressResponse.from(campaignStats)
                )
        );
    }
}
