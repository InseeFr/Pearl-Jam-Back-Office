package fr.insee.pearljam.api.campaign.dto;

import fr.insee.pearljam.domain.campaign.model.Campaign;
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
