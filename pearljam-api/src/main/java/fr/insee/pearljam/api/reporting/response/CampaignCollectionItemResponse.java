package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CampaignCollectionItem")
public record CampaignCollectionItemResponse(
        String campaignId,
        String campaignLabel,
        long allocated,
        CollectionRatesResponse rates,
        ContactOutcomesProgressResponse outcomes,
        ClosingCausesProgressResponse closingCauses
) {
}
