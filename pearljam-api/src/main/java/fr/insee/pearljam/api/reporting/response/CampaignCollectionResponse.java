package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "CampaignCollection")
public record CampaignCollectionResponse(
        String campaignId,
        String campaignLabel,
        long allocated,
        CollectionRatesResponse rates,
        ContactOutcomesProgressResponse outcomes,
        ClosingCausesProgressResponse closingCauses,
        Instant updatedAt
) {
}
