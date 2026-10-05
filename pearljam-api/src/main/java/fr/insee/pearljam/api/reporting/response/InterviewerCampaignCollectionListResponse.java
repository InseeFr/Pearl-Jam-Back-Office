package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "InterviewerCampaignCollectionList")
public record InterviewerCampaignCollectionListResponse(
        List<InterviewerCampaignCollectionItemResponse> campaigns,
        long updatedAt
) {
}
