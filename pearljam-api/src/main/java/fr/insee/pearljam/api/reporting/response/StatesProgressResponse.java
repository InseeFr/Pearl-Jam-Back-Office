package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StatesProgress")
public record StatesProgressResponse(
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
    public static StatesProgressResponse from(AbstractDailyStats dailyStats) {
        return new StatesProgressResponse(
                dailyStats.getAllocatedCount(),
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
