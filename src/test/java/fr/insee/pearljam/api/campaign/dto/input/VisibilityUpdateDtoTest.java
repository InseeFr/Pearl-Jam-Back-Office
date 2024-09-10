package fr.insee.pearljam.api.campaign.dto.input;

import fr.insee.pearljam.domain.campaign.model.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VisibilityUpdateDtoTest {

    @Test
    @DisplayName("Should return model object")
    void testToModel() {
        // Given
        Long managementStartDate = 1627845600000L;
        Long interviewerStartDate = 1627932000000L;
        Long identificationPhaseStartDate = 1628018400000L;
        Long collectionStartDate = 1628104800000L;
        Long collectionEndDate = 1628191200000L;
        Long endDate = 1628277600000L;
        String campaignId = "campaign1";
        String ouId = "OU1";

        VisibilityUpdateDto dto = new VisibilityUpdateDto(
                managementStartDate, interviewerStartDate, identificationPhaseStartDate,
                collectionStartDate, collectionEndDate, endDate, true);

        // When
        Visibility model = VisibilityUpdateDto.toModel(dto, campaignId, ouId);

        // Then
        assertThat(model.campaignId()).isEqualTo(campaignId);
        assertThat(model.organizationalUnitId()).isEqualTo(ouId);
        assertThat(model.managementStartDate()).isEqualTo(managementStartDate);
        assertThat(model.interviewerStartDate()).isEqualTo(interviewerStartDate);
        assertThat(model.identificationPhaseStartDate()).isEqualTo(identificationPhaseStartDate);
        assertThat(model.collectionStartDate()).isEqualTo(collectionStartDate);
        assertThat(model.collectionEndDate()).isEqualTo(collectionEndDate);
        assertThat(model.endDate()).isEqualTo(endDate);
    }
}
