package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignProgressList")
public record CampaignProgressListResponse(
        List<CampaignProgressItemResponse> campaigns,
        long updatedAt
) {
}
