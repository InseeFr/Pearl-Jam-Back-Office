package fr.insee.pearljam.domain.reporting.readmodel.progress;

public record CampaignSummaryProgress(String campaignId,
                                      String campaignLabel,
                                      Long collectionStartDate,
                                      Long collectionEndDate,
                                      Long endDate,
                                      CampaignPhase campaignPhase,
                                      StatesSummaryProgress states) {


}