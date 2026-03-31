package fr.insee.pearljam.domain.reporting.readmodel;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.stats.InterviewerDailyStats;

import java.util.List;

public record CampaignProgressionByInterviewers(
        List<Interviewer> interviewers,
        OrganizationUnit site,
        Campaign campaign) {

    public record CampaignProgressionByInterviewersSurveyUnits(
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
        public static CampaignProgressionByInterviewersSurveyUnits from(InterviewerDailyStats interviewerDailyStats) {
            return new CampaignProgressionByInterviewersSurveyUnits(
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

        public static CampaignProgressionByInterviewersSurveyUnits from(CampaignDailyStats campaignDailyStats) {
            return new CampaignProgressionByInterviewersSurveyUnits(
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
            CampaignProgressionByInterviewersSurveyUnits surveyUnits
    ) {}

    public record OrganizationUnit(
            float progressRate,
            CampaignProgressionByInterviewersSurveyUnits surveyUnits
    ) {}

    public record Campaign(
            float progressRate,
            CampaignProgressionByInterviewersSurveyUnits surveyUnits
    ) {}
}
