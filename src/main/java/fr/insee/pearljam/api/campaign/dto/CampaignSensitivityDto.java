package fr.insee.pearljam.api.campaign.dto;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import jakarta.validation.constraints.NotBlank;


public record CampaignSensitivityDto(
        @NotBlank
        String id,
        Boolean sensitivity)
{
    public static CampaignSensitivityDto fromModel(CampaignDB campaignDB) {
        return new CampaignSensitivityDto(campaignDB.getId(),
                campaignDB.getSensitivity()
        );
    }
}
