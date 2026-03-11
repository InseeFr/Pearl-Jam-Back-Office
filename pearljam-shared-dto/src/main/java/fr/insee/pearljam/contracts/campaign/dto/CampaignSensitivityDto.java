package fr.insee.pearljam.contracts.campaign.dto;

import jakarta.validation.constraints.NotBlank;


public record CampaignSensitivityDto(
        @NotBlank
        String id,
        Boolean sensitivity)
{
    public static CampaignSensitivityDto fromModel(String id, Boolean sensitivity) {
        return new CampaignSensitivityDto(id, sensitivity);
    }
}
