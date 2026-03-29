package fr.insee.pearljam.domain.reporting.readmodel;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;

public record CampaignProgression(String campaignId, String campaignLabel, float progressRate,
                                  SurveyUnits surveyUnits) {

    public record SurveyUnits(
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
}
