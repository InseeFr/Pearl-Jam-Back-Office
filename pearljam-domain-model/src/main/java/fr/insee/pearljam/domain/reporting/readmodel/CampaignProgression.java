package fr.insee.pearljam.domain.reporting.readmodel;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;

public record CampaignProgression(String campaignId, String campaignLabel, float progressRate,
                                  CampaignProgressionSurveyUnits surveyUnits) {

    public record CampaignProgressionSurveyUnits(
            Long allocated,
            Long notStarted,
            Long inProgress,
            Long pendingTransmission,
            Long toReview,
            Long validated,
            Long preparingContact,
            Long atLeastOneContact,
            Long appoLongmentScheduled,
            Long started,
            Long noticeLetter,
            Long reminderLetter
    ) {
        public static CampaignProgressionSurveyUnits from(CampaignDailyStats campaignDailyStats) {
            return new CampaignProgressionSurveyUnits(
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
}
