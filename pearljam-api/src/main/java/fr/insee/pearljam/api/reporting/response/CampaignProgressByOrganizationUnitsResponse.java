package fr.insee.pearljam.api.reporting.response;

import java.util.List;

public record CampaignProgressByOrganizationUnitsResponse(
        List<OrganizationUnit> organizationUnits,
        Campaign campaign
) {
    public record OrganizationUnit(
            String organizationUnitLabel,
            float progressRate,
            StatesProgressResponse states,
            CommunicationsProgressResponse communications
    ) {
    }

    public record Campaign(
            float progressRate,
            StatesProgressResponse states,
            CommunicationsProgressResponse communications
    ) {
    }
}
