package fr.insee.pearljam.domain.reporting.readmodel;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;

public record StatesSummaryProgress(long allocated,
                                                    long toProcessInterviewer,
                                                    long toReview,
                                                    long completed,
                                                    long notAssigned) {

    public static StatesSummaryProgress from(CampaignDailyStats campaignDailyStats) {
        return new StatesSummaryProgress(
                campaignDailyStats.getTotal(),
                campaignDailyStats.getToProcessInterviewer(),
                campaignDailyStats.getTbrCount(),
                campaignDailyStats.getValidated(),
                campaignDailyStats.getUnaffected()
        );
    }
}
