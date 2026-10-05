package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "InterviewerCampaignCollectionItem")
public record InterviewerCampaignCollectionItemResponse(
        String campaignId,
        String campaignLabel,
        long allocatedInterviewers,
        CollectionRatesResponse rates,
        ContactOutcomesProgressResponse outcomes,
        ClosingCausesProgressResponse closingCauses
) {
}
