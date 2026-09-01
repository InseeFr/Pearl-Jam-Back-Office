package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignProgressByInterviewers")
public record CampaignProgressByInterviewersResponse(
        List<Interviewer> interviewers,
        OrganizationUnit site,
        Campaign campaign
) {
    @Schema(name = "CampaignProgressByInterviewersInterviewer")
    public record Interviewer(
            String interviewerLabel,
            float progressRate,
            StatesProgressResponse states,
            CommunicationsProgressResponse communications,
            long updatedAt
    ) {
    }

    @Schema(name = "CampaignProgressByInterviewersOU")
    public record OrganizationUnit(
            float progressRate,
            long unaffected,
            StatesProgressResponse states,
            CommunicationsProgressResponse communications,
            long updatedAt
    ) {
    }

    @Schema(name = "CampaignProgressByInterviewersCampaign")
    public record Campaign(
            float progressRate,
            StatesProgressResponse states,
            CommunicationsProgressResponse communications,
            long updatedAt
    ) {
    }
}
