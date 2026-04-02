package fr.insee.pearljam.domain.reporting.readmodel.progress;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;

public record StatesProgress(
        long allocated,
        long notStarted,
        long inProgress,
        long pendingTransmission,
        long toReview,
        long validated,
        long preparingContact,
        long withContact,
        long withAppointment,
        long started
) {
    public static StatesProgress from(CampaignDailyStats campaignDailyStats) {
        return new StatesProgress(
                campaignDailyStats.getAllocatedStateCount(),
                campaignDailyStats.getVicStateCount(),
                campaignDailyStats.getInProgressStateCount(),
                campaignDailyStats.getWftStateCount(),
                campaignDailyStats.getTbrStateCount(),
                campaignDailyStats.getCompletedStateCount(),
                campaignDailyStats.getPrcStateCount(),
                campaignDailyStats.getAocStateCount(),
                campaignDailyStats.getApsStateCount(),
                campaignDailyStats.getInsStateCount()
        );
    }

    public static StatesProgress from(InterviewerDailyStats interviewerDailyStats) {
        return new StatesProgress(
                interviewerDailyStats.getAllocatedStateCount(),
                interviewerDailyStats.getVicStateCount(),
                interviewerDailyStats.getInProgressStateCount(),
                interviewerDailyStats.getWftStateCount(),
                interviewerDailyStats.getTbrStateCount(),
                interviewerDailyStats.getCompletedStateCount(),
                interviewerDailyStats.getPrcStateCount(),
                interviewerDailyStats.getAocStateCount(),
                interviewerDailyStats.getApsStateCount(),
                interviewerDailyStats.getInsStateCount()
        );
    }
}
