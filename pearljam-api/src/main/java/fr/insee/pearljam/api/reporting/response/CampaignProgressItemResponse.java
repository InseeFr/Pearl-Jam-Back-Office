package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CampaignProgressItem")
public record CampaignProgressItemResponse(
        String campaignId,
        String campaignLabel,
        float progressRate,
        StatesProgressResponse states,
        CommunicationsProgressResponse communications
) {
}
