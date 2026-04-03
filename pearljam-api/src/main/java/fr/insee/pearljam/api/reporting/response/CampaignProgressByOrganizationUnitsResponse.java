package fr.insee.pearljam.api.reporting.response;

import fr.insee.pearljam.domain.reporting.readmodel.progress.CommunicationsProgress;
import fr.insee.pearljam.domain.reporting.readmodel.progress.StatesProgress;

import java.util.List;

public record CampaignProgressByOrganizationUnitsResponse(
        List<OrganizationUnit> organizationUnits,
        Campaign campaign
) {
    public record OrganizationUnit(
            String organizationUnitLabel,
            float progressRate,
            StatesProgress states,
            CommunicationsProgress communications
    ) {
    }

    public record Campaign(
            float progressRate,
            StatesProgress states,
            CommunicationsProgress communications
    ) {
    }
}
