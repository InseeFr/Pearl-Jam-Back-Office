package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CampaignProgressInterviewer")
public record InterviewerCampaignsProgressResponse(
        String campaignId,
        String campaignLabel,
        float progressRate,
        StatesInterviewerProgressResponse states,
        CommunicationsProgressResponse communications
) {
}
