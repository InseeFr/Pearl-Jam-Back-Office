package fr.insee.pearljam.api.reporting.response;

public record CampaignProgressResponse(
        String campaignId,
        String campaignLabel,
        float progressRate,
        StatesProgressResponse states,
        CommunicationsProgressResponse communications
) {
}
