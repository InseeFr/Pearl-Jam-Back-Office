package fr.insee.pearljam.domain.reporting.readmodel;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;

import java.util.List;

public record CampaignProgressionByInterviewers(
        List<Interviewer> interviewers,
        OrganizationUnit site,
        Campaign campaign) {

    public record SurveyUnits(
            Long allocated,
            Long notStarted,
            Long inProgress,
            Long pendingTransmission,
            Long toReview,
            Long validated,
            Long progressPreparation,
            Long withContact,
            Long withAppointment,
            Long started,
            Long noticeLetter,
            Long reminderLetter
    ) {
        public static SurveyUnits from(InterviewerDailyStats interviewerDailyStats) {
            return new SurveyUnits(
                    interviewerDailyStats.getTotal(),
                    interviewerDailyStats.getVicCount(),
                    interviewerDailyStats.getInProgress(),
                    interviewerDailyStats.getWftCount(),
                    interviewerDailyStats.getTbrCount(),
                    interviewerDailyStats.getValidated(),
                    interviewerDailyStats.getPrcCount(),
                    interviewerDailyStats.getAocCount(),
                    interviewerDailyStats.getApsCount(),
                    interviewerDailyStats.getInsCount(),
                    interviewerDailyStats.getNoticeCount(),
                    interviewerDailyStats.getReminderCount()
            );
        }

        public static SurveyUnits from(CampaignDailyStats campaignDailyStats) {
            return new SurveyUnits(
                    campaignDailyStats.getTotal(),
                    campaignDailyStats.getVicCount(),
                    campaignDailyStats.getInProgress(),
                    campaignDailyStats.getWftCount(),
                    campaignDailyStats.getTbrCount(),
                    campaignDailyStats.getValidated(),
                    campaignDailyStats.getPrcCount(),
                    campaignDailyStats.getAocCount(),
                    campaignDailyStats.getApsCount(),
                    campaignDailyStats.getInsCount(),
                    campaignDailyStats.getNoticeCount(),
                    campaignDailyStats.getReminderCount()
            );
        }
    }

    public record Interviewer(
            String interviewerLabel,
            float progressRate,
            SurveyUnits surveyUnits
    ) {}

    public record OrganizationUnit(
            float progressRate,
            SurveyUnits surveyUnits
    ) {}

    public record Campaign(
            float progressRate,
            SurveyUnits surveyUnits
    ) {}
}
