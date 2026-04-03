package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.progress.CommunicationsProgress;
import fr.insee.pearljam.domain.reporting.readmodel.progress.StatesProgress;

import java.util.List;

public record CampaignProgressByInterviewersResponse(
        List<Interviewer> interviewers,
        OrganizationUnit site,
        Campaign campaign
) {
    public record Interviewer(
            String interviewerLabel,
            float progressRate,
            StatesProgress states,
            CommunicationsProgress communications
    ) {
    }

    public record OrganizationUnit(
            float progressRate,
            StatesProgress states,
            CommunicationsProgress communications
    ) {
    }

    public record Campaign(
            long unaffected,
            float progressRate,
            StatesProgress states,
            CommunicationsProgress communications
    ) {
    }
}
