package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignCollectionList")
public record CampaignCollectionListResponse(
        List<CampaignCollectionItemResponse> campaigns,
        long updatedAt
) {
}
