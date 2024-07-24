package fr.insee.pearljam.api.campaign.dto.input;

import fr.insee.pearljam.domain.campaign.model.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VisibilityCampaignUpdateDtoTest {

    @Test
    @DisplayName("Should return model objects")
    void testToModel() {
        // Given
        String campaignId = "campaign1";
        VisibilityCampaignUpdateDto dto1 = new VisibilityCampaignUpdateDto(1L, 2L, 3L, 4L, 5L, 6L, "OU1");
        VisibilityCampaignUpdateDto dto2 = new VisibilityCampaignUpdateDto(11L, 12L, 13L, 14L, 15L, 16L, "OU2");
        List<VisibilityCampaignUpdateDto> dtoList = List.of(dto1, dto2);

        // When
        List<Visibility> modelList = VisibilityCampaignUpdateDto.toModel(dtoList, campaignId);

        // Then
        assertThat(modelList).hasSize(2);

        Visibility firstVisibility = modelList.getFirst();
        assertThat(firstVisibility.campaignId()).isEqualTo(campaignId);
        assertThat(firstVisibility.organizationalUnitId()).isEqualTo("OU1");
        assertThat(firstVisibility.managementStartDate()).isEqualTo(1L);
        assertThat(firstVisibility.interviewerStartDate()).isEqualTo(2L);
        assertThat(firstVisibility.identificationPhaseStartDate()).isEqualTo(3L);
        assertThat(firstVisibility.collectionStartDate()).isEqualTo(4L);
        assertThat(firstVisibility.collectionEndDate()).isEqualTo(5L);
        assertThat(firstVisibility.endDate()).isEqualTo(6L);

        Visibility lastVisibility = modelList.getLast();
        assertThat(lastVisibility.campaignId()).isEqualTo(campaignId);
        assertThat(lastVisibility.organizationalUnitId()).isEqualTo("OU2");
        assertThat(lastVisibility.managementStartDate()).isEqualTo(11L);
        assertThat(lastVisibility.interviewerStartDate()).isEqualTo(12L);
        assertThat(lastVisibility.identificationPhaseStartDate()).isEqualTo(13L);
        assertThat(lastVisibility.collectionStartDate()).isEqualTo(14L);
        assertThat(lastVisibility.collectionEndDate()).isEqualTo(15L);
        assertThat(lastVisibility.endDate()).isEqualTo(16L);
    }
}
