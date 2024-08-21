package fr.insee.pearljam.domain.campaign.service;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.campaign.service.dummy.VisibilityFakeRepository;
import fr.insee.pearljam.domain.exception.VisibilityHasInvalidDatesException;
import fr.insee.pearljam.domain.exception.VisibilityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VisibilityServiceImplTest {

    private VisibilityFakeRepository visibilityRepository;
    private VisibilityServiceImpl visibilityService;
    private Visibility visibility1, visibility2, visibility3;

    @BeforeEach
    void setUp() {
        visibilityRepository = new VisibilityFakeRepository();
        visibilityService = new VisibilityServiceImpl(visibilityRepository);
        visibility1 = new Visibility("campaign1", "OU1",
                1627845600000L, 1627932000000L, 1628018400000L, 1628104800000L, 1628191200000L, 1628277600000L);
        visibility2 = new Visibility("campaign2", "OU2",
                1627845600000L, 1627932000000L, 1628018400000L, 1628104800000L, 1628191200000L, 1628277600000L);
        visibility3 = new Visibility("campaign1", "OU2",
                1627845600000L, 1627932000000L, 1628018400000L, 1628104800000L, 1628191200000L, 1628277600000L);
        visibilityRepository.save(visibility1);
        visibilityRepository.save(visibility2);
        visibilityRepository.save(visibility3);
    }

    @Test
    @DisplayName("Should return visibilities")
    void shouldReturnVisibilitiesForValidCampaignId() {
        List<Visibility> visibilities = visibilityService.findVisibilities(visibility1.campaignId());

        assertThat(visibilities)
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(visibility1, visibility3);
    }

    @Test
    @DisplayName("Should update visibility with valid data")
    void shouldUpdateVisibilityWithValidData() throws VisibilityNotFoundException, VisibilityHasInvalidDatesException {
        Visibility visibilityToUpdate = new Visibility(visibility1.campaignId(), visibility1.organizationalUnitId(),
                1627845600001L, 1627932000001L, 1628018400001L, 1628104800001L, 1628191200001L, 1628277600001L);

        visibilityService.updateVisibility(visibilityToUpdate);

        Optional<Visibility> mergedVisibility = visibilityRepository
                .findVisibility(visibilityToUpdate.campaignId(), visibilityToUpdate.organizationalUnitId());
        assertThat(mergedVisibility)
                .contains(visibilityToUpdate);
    }

    @Test
    @DisplayName("Should throw VisibilityNotFoundException if visibility to update does not exist")
    void shouldThrowVisibilityNotFoundExceptionIfVisibilityToUpdateDoesNotExist() {
        Visibility visibilityToUpdate = new Visibility("invalid-campaign", "invalid-ou",
                1627845600000L, 1627932000000L, 1628018400000L,
                1628104800000L, 1628191200000L, 1628277600000L);

        assertThatThrownBy(() -> visibilityService.updateVisibility(visibilityToUpdate))
                .isInstanceOf(VisibilityNotFoundException.class)
                .hasMessage(VisibilityNotFoundException.MESSAGE);
    }

    @Test
    @DisplayName("Should return CampaignVisibility for valid campaignId and organizational unit ids")
    void shouldReturnCampaignVisibilityForValidCampaignIdAndOuIds() {
        String campaignId = "campaign1";
        List<String> ouIds = List.of("ou1", "ou2");

        CampaignVisibility campaignVisibility = visibilityService.getCampaignVisibility(campaignId, ouIds);

        assertThat(campaignVisibility)
                .isEqualTo(VisibilityFakeRepository.CAMPAIGN_VISIBILITY);
    }
}
