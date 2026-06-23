package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CampaignProgress")
public record CampaignProgressResponse(
        String campaignId,
        String campaignLabel,
        float progressRate,
        StatesProgressResponse states,
        CommunicationsProgressResponse communications
) {
}
