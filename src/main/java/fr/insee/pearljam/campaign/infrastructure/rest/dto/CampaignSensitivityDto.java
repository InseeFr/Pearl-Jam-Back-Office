package fr.insee.pearljam.campaign.infrastructure.rest.dto;

import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.Campaign;
import jakarta.validation.constraints.NotBlank;


public record CampaignSensitivityDto(
        @NotBlank
        String id,
        Boolean sensitivity)
{
    public static CampaignSensitivityDto fromModel(Campaign campaignDB) {
        return new CampaignSensitivityDto(campaignDB.getId(),
                campaignDB.getSensitivity()
        );
    }
}