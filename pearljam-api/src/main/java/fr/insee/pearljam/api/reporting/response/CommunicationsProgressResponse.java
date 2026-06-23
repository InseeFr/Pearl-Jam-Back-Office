package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.AbstractDailyStats;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CommunicationsProgress")
public record CommunicationsProgressResponse(
        long noticeLetter,
        long reminderLetter
) {
    public static CommunicationsProgressResponse from(AbstractDailyStats dailyStats) {
        return new CommunicationsProgressResponse(
                dailyStats.getNoticeCommunicationCount(),
                dailyStats.getReminderCommunicationCount()
        );
    }
}
