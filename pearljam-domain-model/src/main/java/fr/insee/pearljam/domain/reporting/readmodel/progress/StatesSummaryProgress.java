package fr.insee.pearljam.domain.reporting.readmodel.progress;

import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;

public record StatesSummaryProgress(long allocated,
                                                    long toProcessInterviewer,
                                                    long toReview,
                                                    long completed,
                                                    long notAssigned) {

    public static StatesSummaryProgress from(CampaignDailyStats campaignDailyStats) {
        return new StatesSummaryProgress(
                campaignDailyStats.getAllocatedCount(),
                campaignDailyStats.getToProcessInterviewerStateCount(),
                campaignDailyStats.getTbrStateCount(),
                campaignDailyStats.getCompletedStateCount(),
                campaignDailyStats.getUnaffectedCount()
        );
    }
}
