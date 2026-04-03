package fr.insee.pearljam.domain.campaign.model;


import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;

import java.util.List;

public record CampaignOrganization(
        String campaignId,
        String campaignLabel,
        long identificationPhaseStartDate,
        long collectionStartDate,
        long collectionEndDate,
        long endDate,
        CampaignPhase phase,
        List<Referent> referents,
        List<Interviewer> interviewers,
        CampaignOrganizationSurveyUnitCount surveyUnits
) {
    public record Referent(
            String firstName,
            String lastName,
            String phoneNumber,
            String role
    ) {}

    public record Interviewer(
            String id,
            String label,
            Long surveyUnits
    ) {}

    public record CampaignOrganizationSurveyUnitCount(
            Long total,
            Long notAffected
    ) {}
}
