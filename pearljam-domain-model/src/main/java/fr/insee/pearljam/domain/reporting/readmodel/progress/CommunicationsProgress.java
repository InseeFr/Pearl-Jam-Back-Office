package fr.insee.pearljam.domain.reporting.readmodel.progress;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;

public record CommunicationsProgress(
        long noticeLetter,
        long reminderLetter
) {
    public static CommunicationsProgress from(CampaignDailyStats campaignDailyStats) {
        return new CommunicationsProgress(
                campaignDailyStats.getNoticeCommunicationCount(),
                campaignDailyStats.getReminderCommunicationCount()
        );
    }

    public static CommunicationsProgress from(InterviewerDailyStats interviewerDailyStats) {
        return new CommunicationsProgress(
                interviewerDailyStats.getNoticeCommunicationCount(),
                interviewerDailyStats.getReminderCommunicationCount()
        );
    }
}
