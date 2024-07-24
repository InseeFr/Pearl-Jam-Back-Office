package fr.insee.pearljam.api.campaign.dto.output;

import fr.insee.pearljam.domain.campaign.model.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VisibilityCampaignDtoTest {

    @Test
    @DisplayName("Should return dto objects from models")
    void testFromModel() {
        // Given
        Visibility visibility1 = new Visibility("campaign-id", "OU1", 1627845600000L, 1627932000000L, 1628018400000L, 1628104800000L, 1628191200000L, 1628277600000L);
        Visibility visibility2 = new Visibility("campaign-id", "OU2", 1627845600000L, 1627932000000L, 1628018400000L, 1628104800000L, 1628191200000L, 1628277600000L);
        List<Visibility> visibilityList = List.of(visibility1, visibility2);

        // When
        List<VisibilityCampaignDto> dtoList = VisibilityCampaignDto.fromModel(visibilityList);

        // Then
        assertThat(dtoList).hasSize(2);

        assertThat(dtoList.getFirst().organizationalUnit()).isEqualTo("OU1");
        assertThat(dtoList.getFirst().managementStartDate()).isEqualTo(1627845600000L);
        assertThat(dtoList.getFirst().interviewerStartDate()).isEqualTo(1627932000000L);
        assertThat(dtoList.getFirst().identificationPhaseStartDate()).isEqualTo(1628018400000L);
        assertThat(dtoList.getFirst().collectionStartDate()).isEqualTo(1628104800000L);
        assertThat(dtoList.getFirst().collectionEndDate()).isEqualTo(1628191200000L);
        assertThat(dtoList.getFirst().endDate()).isEqualTo(1628277600000L);

        assertThat(dtoList.getLast().organizationalUnit()).isEqualTo("OU2");
        assertThat(dtoList.getLast().managementStartDate()).isEqualTo(1627845600000L);
        assertThat(dtoList.getLast().interviewerStartDate()).isEqualTo(1627932000000L);
        assertThat(dtoList.getLast().identificationPhaseStartDate()).isEqualTo(1628018400000L);
        assertThat(dtoList.getLast().collectionStartDate()).isEqualTo(1628104800000L);
        assertThat(dtoList.getLast().collectionEndDate()).isEqualTo(1628191200000L);
        assertThat(dtoList.getLast().endDate()).isEqualTo(1628277600000L);
    }
}