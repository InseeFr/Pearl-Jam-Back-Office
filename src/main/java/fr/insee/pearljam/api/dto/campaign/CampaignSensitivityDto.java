package fr.insee.pearljam.api.dto.campaign;

import fr.insee.pearljam.api.domain.Campaign;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampaignSensitivityDto {

    @NotBlank
    private String id;
    private String sourceId;
    private int year;
    private String period;
    private String dataCollectionTarget;
    private boolean sensitivity;

    public CampaignSensitivityDto(String id, Boolean sensitivity) {
        this.id = id;
        this.sensitivity = sensitivity;
    }

    public static CampaignSensitivityDto fromModel(Campaign campaignDB) {
        return new CampaignSensitivityDto(campaignDB.getId(),
                campaignDB.getSensitivity()
        );
    }

}