package fr.insee.pearljam.domain.campaign.model;

import java.util.List;

public record CampaignOrganization(
        String campaignId,
        String campaignLabel,
        long identificationPhaseStartDate,
        long collectionStartDate,
        long collectionEndDate,
        long endDate,
        Phase phase,
        List<Referent> referents,
        Interviewer interviewers,
        SurveyUnits surveyUnits
) {
    public record Referent(
            String firstName,
            String lastName,
            String phoneNumber,
            Role role
    ) {}
    public record Interviewer(
            String id,
            String label,
            int surveyUnits
    ) {}
    public record SurveyUnits(
            int total,
            int abandoned,
            int notAffected
    ) {}
    public enum Phase {
        INITIAL_ASSIGNMENT,
        COLLECTION_IN_PROGRESS,
        COLLECTION_COMPLETED
    }
    public enum Role {
        PRIMARY
    }
}
