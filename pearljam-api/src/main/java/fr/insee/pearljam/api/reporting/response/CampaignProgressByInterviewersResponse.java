package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignProgressByInterviewers")
public record CampaignProgressByInterviewersResponse(
        List<Interviewer> interviewers,
        OrganizationUnit site,
        Campaign campaign
) {
    public record Interviewer(
            String interviewerLabel,
            float progressRate,
            StatesProgressResponse states,
            CommunicationsProgressResponse communications
    ) {
    }

    public record OrganizationUnit(
            float progressRate,
            StatesProgressResponse states,
            CommunicationsProgressResponse communications
    ) {
    }

    public record Campaign(
            long unaffected,
            float progressRate,
            StatesProgressResponse states,
            CommunicationsProgressResponse communications
    ) {
    }
}
