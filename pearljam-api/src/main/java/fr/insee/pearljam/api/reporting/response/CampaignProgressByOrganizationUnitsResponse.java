package fr.insee.pearljam.api.reporting.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CampaignProgressByOrganizationUnits")
public record CampaignProgressByOrganizationUnitsResponse(
        List<OrganizationUnit> organizationUnits,
        Campaign campaign
) {
    @Schema(name = "CampaignProgressByOrganizationUnitsOU")
    public record OrganizationUnit(
            String organizationUnitLabel,
            float progressRate,
            StatesProgressResponse states,
            CommunicationsProgressResponse communications,
            long updatedAt
    ) {
    }

    @Schema(name = "CampaignProgressByOrganizationUnitsCampaign")
    public record Campaign(
            float progressRate,
            StatesProgressResponse states,
            CommunicationsProgressResponse communications,
            long updatedAt
    ) {
    }
}
