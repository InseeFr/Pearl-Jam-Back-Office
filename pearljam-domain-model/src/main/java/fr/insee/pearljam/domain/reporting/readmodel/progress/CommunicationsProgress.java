package fr.insee.pearljam.domain.reporting.readmodel.progress;

import fr.insee.pearljam.domain.reporting.readmodel.stats.AbstractDailyStats;

public record CommunicationsProgress(
        long noticeLetter,
        long reminderLetter
) {
    public static CommunicationsProgress from(AbstractDailyStats dailyStats) {
        return new CommunicationsProgress(
                dailyStats.getNoticeCommunicationCount(),
                dailyStats.getReminderCommunicationCount()
        );
    }
}
