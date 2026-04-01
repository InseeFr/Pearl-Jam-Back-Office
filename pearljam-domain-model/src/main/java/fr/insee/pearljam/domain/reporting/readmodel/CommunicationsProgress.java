package fr.insee.pearljam.domain.reporting.readmodel;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;

public record CommunicationsProgress(
        Long noticeLetter,
        Long reminderLetter
) {
    public static CommunicationsProgress from(CampaignDailyStats campaignDailyStats) {
        return new CommunicationsProgress(
                campaignDailyStats.getNoticeCount(),
                campaignDailyStats.getReminderCount()
        );
    }

    public static CommunicationsProgress from(InterviewerDailyStats interviewerDailyStats) {
        return new CommunicationsProgress(
                interviewerDailyStats.getNoticeCount(),
                interviewerDailyStats.getReminderCount()
        );
    }
}
