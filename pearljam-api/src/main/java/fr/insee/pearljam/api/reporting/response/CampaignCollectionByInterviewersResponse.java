package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignCollectionByInterviewers")
public record CampaignCollectionByInterviewersResponse(
        List<Interviewer> interviewers,
        OrganizationUnit site,
        Campaign campaign) {

    public static CampaignCollectionByInterviewersResponse from(
            List<InterviewerDailyStats> interviewerDailyStats,
            CampaignDailyStats campaignOusDailyStats,
            CampaignDailyStats campaignDailyStats) {

        List<Interviewer> interviewers = interviewerDailyStats.stream()
                .map(intDailyStats -> new Interviewer(
                        intDailyStats.getInterviewerFirstName() + " " + intDailyStats.getInterviewerLastName(),
                        intDailyStats.getAllocatedCount(),
                        CollectionRatesResponse.from(intDailyStats),
                        ContactOutcomesProgressResponse.from(intDailyStats),
                        ClosingCausesProgressResponse.from(intDailyStats)
                ))
                .toList();

        OrganizationUnit site = new OrganizationUnit(
                campaignOusDailyStats.getAllocatedCount(),
                campaignOusDailyStats.getUnaffectedCount(),
                CollectionRatesResponse.from(campaignOusDailyStats),
                ContactOutcomesProgressResponse.from(campaignOusDailyStats),
                ClosingCausesProgressResponse.from(campaignOusDailyStats)
        );

        Campaign campaign = new Campaign(
                campaignDailyStats.getAllocatedCount(),
                CollectionRatesResponse.from(campaignDailyStats),
                ContactOutcomesProgressResponse.from(campaignDailyStats),
                ClosingCausesProgressResponse.from(campaignDailyStats));
        return new CampaignCollectionByInterviewersResponse(interviewers, site, campaign);
    }

    @Schema(name = "CampaignCollectionByInterviewersInterviewer")
    public record Interviewer(
            String interviewerLabel,
            long allocated,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}

    @Schema(name = "CampaignCollectionByInterviewersOU")
    public record OrganizationUnit(
            long allocated,
            long unaffected,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}

    @Schema(name = "CampaignCollectionByInterviewersCampaign")
    public record Campaign(
            long allocated,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}
}
