package fr.insee.pearljam.api.reporting.response;

import java.util.List;

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
