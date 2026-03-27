package fr.insee.pearljam.domain.reporting.model;


public record CampaignSummaryWithStateCount(String campaignId, String campaignLabel, Long collectionStartDate,
                                            Long collectionEndDate, Long endDate, CampaignPhase campaignPhase,
                                            SurveyUnits surveyUnits) {

    public record SurveyUnits(Long allocated, Long toProcessInterviewer, Long toAssign, Long toFollowUp, Long toReview,
                              Long completed) {
    }
}