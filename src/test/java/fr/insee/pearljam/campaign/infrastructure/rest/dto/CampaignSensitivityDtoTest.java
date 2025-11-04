package fr.insee.pearljam.campaign.infrastructure.rest.dto;

import fr.insee.pearljam.campaign.domain.model.ContactAttemptConfiguration;
import fr.insee.pearljam.campaign.domain.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.Campaign;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.IdentificationConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CampaignSensitivityDtoTest {

    @Test
    @DisplayName("Test of creating a sensitive campaign")
    void testCreateCampaignSensitivity() {
        // Given
        Campaign campaign = new Campaign("id", "label", IdentificationConfiguration.HOUSEF2F,
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