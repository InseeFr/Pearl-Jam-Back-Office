package fr.insee.pearljam.domain.reporting.readmodel.progress;

import fr.insee.pearljam.domain.reporting.readmodel.stats.AbstractDailyStats;

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
    public static StatesProgress from(AbstractDailyStats dailyStats) {
        return new StatesProgress(
                dailyStats.getAllocatedStateCount(),
                dailyStats.getVicStateCount(),
                dailyStats.getInProgressStateCount(),
                dailyStats.getWftStateCount(),
                dailyStats.getTbrStateCount(),
                dailyStats.getCompletedStateCount(),
                dailyStats.getPrcStateCount(),
                dailyStats.getAocStateCount(),
                dailyStats.getApsStateCount(),
                dailyStats.getInsStateCount()
        );
    }
}
