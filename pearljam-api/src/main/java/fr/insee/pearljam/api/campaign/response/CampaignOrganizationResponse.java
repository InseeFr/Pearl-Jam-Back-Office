package fr.insee.pearljam.api.campaign.response;


import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


@Schema(name = "CampaignOrganization")
public record CampaignOrganizationResponse(
        String campaignId,
        String campaignLabel,
        String campaignEmail,
        long managementStartDate,
        long identificationPhaseStartDate,
        long collectionStartDate,
        long collectionEndDate,
        long endDate,
        CampaignPhase phase,
        List<Referent> referents,
        List<Interviewer> interviewers,
        UserOrganizationUnitsSurveyUnitCount surveyUnits
) {
    @Schema(name = "CampaignOrganizationReferent")
    public record Referent(
            String firstName,
            String lastName,
            String phoneNumber,
            String role
    ) {}

    @Schema(name = "UserOrganizationUnitsInterviewer")
    public record Interviewer(
            String id,
            String label,
            Long surveyUnits
    ) {}

    @Schema(name = "UserOrganizationUnitsSurveyUnitCount")
    public record UserOrganizationUnitsSurveyUnitCount(
            Long totalSite,
            Long notAffected
    ) {}
}
