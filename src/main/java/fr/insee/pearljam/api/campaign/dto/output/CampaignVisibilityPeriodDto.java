package fr.insee.pearljam.api.campaign.dto.output;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibilityPeriod;

public record CampaignVisibilityPeriodDto(
        String id,
        String label,
        Long managementStartDate,
        Long endDate) {

    public static CampaignVisibilityPeriodDto fromDomain(CampaignVisibilityPeriod period) {
        return new CampaignVisibilityPeriodDto(
                period.id(),
                period.label(),
                period.managementStartDate(),
                period.endDate()
        );
    }
}
