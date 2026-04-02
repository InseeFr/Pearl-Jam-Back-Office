package fr.insee.pearljam.domain.reporting.readmodel.progress;


import java.util.List;

public record CampaignProgressByInterviewers(
        List<Interviewer> interviewers,
        OrganizationUnit site,
        Campaign campaign) {

    public record Interviewer(
            String interviewerLabel,
            float progressRate,
            StatesProgress states,
            CommunicationsProgress communications
    ) {}

    public record OrganizationUnit(
            float progressRate,
            StatesProgress states,
            CommunicationsProgress communications
    ) {}

    public record Campaign(
            long unaffected,
            float progressRate,
            StatesProgress states,
            CommunicationsProgress communications
    ) {}
}
