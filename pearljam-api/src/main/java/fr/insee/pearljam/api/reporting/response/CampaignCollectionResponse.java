package fr.insee.pearljam.api.reporting.response;

public record CampaignCollectionResponse(
        String campaignId,
        String campaignLabel,
        long allocated,
        CollectionRatesResponse rates,
        ContactOutcomesProgressResponse outcomes,
        ClosingCausesProgressResponse closingCauses
) {
}
