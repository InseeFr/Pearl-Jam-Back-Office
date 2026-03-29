package fr.insee.pearljam.domain.reporting.readmodel;

import fr.insee.pearljam.domain.reporting.readmodel.stats.CampaignDailyStats;

public record CampaignSummaryProgression(String campaignId,
                                         String campaignLabel,
                                         Long collectionStartDate,
                                         Long collectionEndDate,
                                         Long endDate,
                                         CampaignPhase campaignPhase,
                                         SurveyUnits surveyUnits) {

    public record SurveyUnits(Long allocated,
                              Long toProcessInterviewer,
                              Long toReview,
                              Long completed,
                              Long notAssigned) {

        public static SurveyUnits from(CampaignDailyStats campaignDailyStats) {
            return new SurveyUnits(
                    campaignDailyStats.getTotal(),
                    campaignDailyStats.getToProcessInterviewer(),
                    campaignDailyStats.getTbrCount(),
                    campaignDailyStats.getValidated(),
                    campaignDailyStats.getUnaffected()
            );
        }
    }
}