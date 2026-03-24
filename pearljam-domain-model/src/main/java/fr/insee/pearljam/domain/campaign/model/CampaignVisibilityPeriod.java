package fr.insee.pearljam.domain.campaign.model;

public record CampaignVisibilityPeriod(
        String id,
        String label,
        Long managementStartDate,
        Long endDate
) {
}
