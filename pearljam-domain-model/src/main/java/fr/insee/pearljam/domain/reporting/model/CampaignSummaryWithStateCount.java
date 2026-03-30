package fr.insee.pearljam.domain.reporting.model;


public record CampaignSummaryWithStateCount(String campaignId,
                                            String campaignLabel,
                                            Long collectionStartDate,
                                            Long collectionEndDate,
                                            Long endDate,
                                            CampaignPhase campaignPhase,
                                            CampaignSummaryWithStateCountSurveyUnits surveyUnits) {

    public record CampaignSummaryWithStateCountSurveyUnits(Long allocated,
                                                           Long toProcessInterviewer,
                                                           Long toReview,
                                                           Long completed,
                                                           Long notAssigned) {
    }
}