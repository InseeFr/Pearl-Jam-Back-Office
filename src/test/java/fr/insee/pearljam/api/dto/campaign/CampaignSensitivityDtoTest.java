package fr.insee.pearljam.api.dto.campaign;

import fr.insee.pearljam.api.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CampaignSensitivityDtoTest {

    @Test
    @DisplayName("Test of creating a sensitive campaign")
    void testCreateCampaignSensitivity() {
        // Given
        Campaign campaign = new Campaign("id", "label", IdentificationConfiguration.IASCO,
                ContactOutcomeConfiguration.F2F, ContactAttemptConfiguration.F2F,
                "email@plop.com", false);

        // When
        CampaignSensitivityDto campaignSensitivityDto = CampaignSensitivityDto.fromModel(campaign);

        // Then
        assertThat(campaignSensitivityDto).isNotNull();
        assertThat(campaign.getId()).isEqualTo(campaignSensitivityDto.id());
        assertThat(campaign.getSensitivity()).isEqualTo(campaignSensitivityDto.sensitivity());
    }
}