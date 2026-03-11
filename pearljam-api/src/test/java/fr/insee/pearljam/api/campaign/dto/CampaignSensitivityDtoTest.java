package fr.insee.pearljam.api.campaign.dto;

import fr.insee.pearljam.domain.campaign.model.*;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignSensitivityDtoTest {

    @Test
    @DisplayName("Test of creating a sensitive campaign")
    void testCreateCampaignSensitivity() {
        // Given
        CampaignDB campaign = new CampaignDB("id", "label", IdentificationConfiguration.HOUSEF2F,
                ContactOutcomeConfiguration.F2F, ContactAttemptConfiguration.F2F,
                "email@plop.com", false, false);

        // When
        CampaignSensitivityDto campaignSensitivityDto =
                CampaignSensitivityDto.fromModel(campaign.getId(), campaign.getSensitivity());

        // Then
        assertThat(campaignSensitivityDto).isNotNull();
        assertThat(campaign.getId()).isEqualTo(campaignSensitivityDto.id());
        assertThat(campaign.getSensitivity()).isEqualTo(campaignSensitivityDto.sensitivity());
    }
}
