package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.InterviewerCampaignDailyStats;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StatesInterviewerProgress")
public record StatesInterviewerProgressResponse(
        long allocatedInterviewers,
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
    public static StatesInterviewerProgressResponse from(InterviewerCampaignDailyStats stats) {
        return new StatesInterviewerProgressResponse(
                stats.getAllocatedCount(),
                stats.getVicStateCount(),
                stats.getInProgressStateCount(),
                stats.getWftStateCount(),
                stats.getTbrStateCount(),
                stats.getCompletedStateCount(),
                stats.getPrcStateCount(),
                stats.getAocStateCount(),
                stats.getApsStateCount(),
                stats.getInsStateCount()
        );
    }
}
