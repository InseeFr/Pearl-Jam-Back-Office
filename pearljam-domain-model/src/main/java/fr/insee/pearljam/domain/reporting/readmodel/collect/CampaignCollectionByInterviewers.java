package fr.insee.pearljam.domain.reporting.readmodel.collect;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;

import java.util.List;

public record CampaignCollectionByInterviewers(
        List<Interviewer> interviewers,
        OrganizationUnit site,
        Campaign campaign) {

    public static CampaignCollectionByInterviewers from(
            List<InterviewerDailyStats> interviewerDailyStats,
            CampaignDailyStats campaignOusDailyStats,
            CampaignDailyStats campaignDailyStats) {

        List<Interviewer> interviewers = interviewerDailyStats.stream()
                .map(intDailyStats -> new Interviewer(
                        intDailyStats.getInterviewerFirstName() + " " + intDailyStats.getInterviewerLastName(),
                        intDailyStats.getAllocatedStateCount(),
                        CollectionRates.from(intDailyStats),
                        ContactOutcomesProgress.from(intDailyStats),
                        ClosingCausesProgress.from(intDailyStats)
                ))
                .toList();

        OrganizationUnit site = new OrganizationUnit(
                campaignOusDailyStats.getAllocatedStateCount(),
                CollectionRates.from(campaignOusDailyStats),
                ContactOutcomesProgress.from(campaignOusDailyStats),
                ClosingCausesProgress.from(campaignOusDailyStats)
        );

        Campaign campaign = new Campaign(
                campaignDailyStats.getAllocatedStateCount(),
                campaignDailyStats.getUnaffectedCount(),
                CollectionRates.from(campaignDailyStats),
                ContactOutcomesProgress.from(campaignDailyStats),
                ClosingCausesProgress.from(campaignDailyStats));
        return new CampaignCollectionByInterviewers(interviewers, site, campaign);
    }

    public record Interviewer(
            String interviewerLabel,
            long allocated,
            CollectionRates rates,
            ContactOutcomesProgress outcomes,
            ClosingCausesProgress closingCauses
    ) {}

    public record OrganizationUnit(
            long allocated,
            CollectionRates rates,
            ContactOutcomesProgress outcomes,
            ClosingCausesProgress closingCauses
    ) {}

    public record Campaign(
            long allocated,
            long unaffected,
            CollectionRates rates,
            ContactOutcomesProgress outcomes,
            ClosingCausesProgress closingCauses
    ) {}
}
