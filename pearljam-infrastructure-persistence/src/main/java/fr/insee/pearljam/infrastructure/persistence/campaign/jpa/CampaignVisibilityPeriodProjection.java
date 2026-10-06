package fr.insee.pearljam.infrastructure.persistence.campaign.jpa;

public interface CampaignVisibilityPeriodProjection {
    String getCampaignId();

    String getCampaignLabel();

    Long getManagementStartDate();

    Long getEndDate();
}
