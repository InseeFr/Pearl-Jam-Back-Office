package fr.insee.pearljam.api.campaign.dto.input;

import fr.insee.pearljam.domain.campaign.model.Visibility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VisibilityCampaignCreateDtoTest {

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
        String organizationalUnit = "OU1";
        String campaignId = "campaign-id";
        String mail = "mail";
        String tel = "tel";

        VisibilityCampaignCreateDto visibilityDto = new VisibilityCampaignCreateDto(
                managementStartDate, interviewerStartDate, identificationPhaseStartDate,
                collectionStartDate, collectionEndDate, endDate, organizationalUnit,
                true, mail, tel);

        // When
        Visibility visibility = VisibilityCampaignCreateDto
                .toModel(List.of(visibilityDto), campaignId)
                .getFirst();

        // Then
        assertThat(visibility.campaignId()).isEqualTo(campaignId);
        assertThat(visibility.organizationalUnitId()).isEqualTo(organizationalUnit);
        assertThat(visibility.managementStartDate()).isEqualTo(managementStartDate);
        assertThat(visibility.interviewerStartDate()).isEqualTo(interviewerStartDate);
        assertThat(visibility.identificationPhaseStartDate()).isEqualTo(identificationPhaseStartDate);
        assertThat(visibility.collectionStartDate()).isEqualTo(collectionStartDate);
        assertThat(visibility.collectionEndDate()).isEqualTo(collectionEndDate);
        assertThat(visibility.endDate()).isEqualTo(endDate);
        assertThat(visibility.useLetterCommunication()).isTrue();
        assertThat(visibility.mail()).isEqualTo(mail);
        assertThat(visibility.tel()).isEqualTo(tel);
    }
}