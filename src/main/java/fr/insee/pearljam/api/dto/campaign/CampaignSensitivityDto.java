package fr.insee.pearljam.api.dto.campaign;

import fr.insee.pearljam.api.domain.Campaign;
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