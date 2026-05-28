package fr.insee.pearljam.domain.campaign;

public record CampaignPreferenceModel (
        String id,
        String label,
        boolean preference
) {}

