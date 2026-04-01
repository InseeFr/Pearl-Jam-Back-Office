package fr.insee.pearljam.domain.reporting.readmodel;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;

public record StatesProgress(
        Long allocated,
        Long notStarted,
        Long inProgress,
        Long pendingTransmission,
        Long toReview,
        Long validated,
        Long preparingContact,
        Long withContact,
        Long withAppointment,
        Long started
) {
    public static StatesProgress from(CampaignDailyStats campaignDailyStats) {
        return new StatesProgress(
                campaignDailyStats.getTotal(),
                campaignDailyStats.getVicCount(),
                campaignDailyStats.getInProgress(),
                campaignDailyStats.getWftCount(),
                campaignDailyStats.getTbrCount(),
                campaignDailyStats.getValidated(),
                campaignDailyStats.getPrcCount(),
                campaignDailyStats.getAocCount(),
                campaignDailyStats.getApsCount(),
                campaignDailyStats.getInsCount()
        );
    }

    public static StatesProgress from(InterviewerDailyStats interviewerDailyStats) {
        return new StatesProgress(
                interviewerDailyStats.getTotal(),
                interviewerDailyStats.getVicCount(),
                interviewerDailyStats.getInProgress(),
                interviewerDailyStats.getWftCount(),
                interviewerDailyStats.getTbrCount(),
                interviewerDailyStats.getValidated(),
                interviewerDailyStats.getPrcCount(),
                interviewerDailyStats.getAocCount(),
                interviewerDailyStats.getApsCount(),
                interviewerDailyStats.getInsCount()
        );
    }
}
