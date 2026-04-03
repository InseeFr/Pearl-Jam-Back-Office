package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;

import java.util.List;

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
                        intDailyStats.getAllocatedStateCount(),
                        CollectionRatesResponse.from(intDailyStats),
                        ContactOutcomesProgressResponse.from(intDailyStats),
                        ClosingCausesProgressResponse.from(intDailyStats)
                ))
                .toList();

        OrganizationUnit site = new OrganizationUnit(
                campaignOusDailyStats.getAllocatedStateCount(),
                CollectionRatesResponse.from(campaignOusDailyStats),
                ContactOutcomesProgressResponse.from(campaignOusDailyStats),
                ClosingCausesProgressResponse.from(campaignOusDailyStats)
        );

        Campaign campaign = new Campaign(
                campaignDailyStats.getAllocatedStateCount(),
                campaignDailyStats.getUnaffectedCount(),
                CollectionRatesResponse.from(campaignDailyStats),
                ContactOutcomesProgressResponse.from(campaignDailyStats),
                ClosingCausesProgressResponse.from(campaignDailyStats));
        return new CampaignCollectionByInterviewersResponse(interviewers, site, campaign);
    }

    public record Interviewer(
            String interviewerLabel,
            long allocated,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}

    public record OrganizationUnit(
            long allocated,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}

    public record Campaign(
            long allocated,
            long unaffected,
            CollectionRatesResponse rates,
            ContactOutcomesProgressResponse outcomes,
            ClosingCausesProgressResponse closingCauses
    ) {}
}
