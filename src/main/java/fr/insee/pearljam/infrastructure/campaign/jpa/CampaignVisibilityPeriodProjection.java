package fr.insee.pearljam.infrastructure.campaign.jpa;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibilityPeriod;

public interface CampaignVisibilityPeriodProjection {
    String getCampaignId();

    String getCampaignLabel();

    Long getManagementStartDate();

    Long getEndDate();

    default CampaignVisibilityPeriod toDomain() {
        return new CampaignVisibilityPeriod(
                getCampaignId(),
                getCampaignLabel(),
                getManagementStartDate(),
                getEndDate());
    }
}
