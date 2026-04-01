package fr.insee.pearljam.domain.reporting.readmodel;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;

public record StatesSummaryProgress(Long allocated,
                                                    Long toProcessInterviewer,
                                                    Long toReview,
                                                    Long completed,
                                                    Long notAssigned) {

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
