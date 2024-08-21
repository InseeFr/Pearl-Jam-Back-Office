package fr.insee.pearljam.domain.campaign.service;

import fr.insee.pearljam.api.campaign.controller.dummy.ReferentFakeService;
import fr.insee.pearljam.api.campaign.controller.dummy.VisibilityFakeService;
import fr.insee.pearljam.api.campaign.dto.input.CampaignCreateDto;
import fr.insee.pearljam.api.campaign.dto.input.CampaignUpdateDto;
import fr.insee.pearljam.api.campaign.dto.input.VisibilityCampaignCreateDto;
import fr.insee.pearljam.api.campaign.dto.input.VisibilityCampaignUpdateDto;
import fr.insee.pearljam.api.domain.*;
import fr.insee.pearljam.api.service.impl.CampaignServiceImpl;
import fr.insee.pearljam.api.surveyunit.controller.dummy.SurveyUnitFakeService;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.domain.campaign.service.dummy.*;
import fr.insee.pearljam.domain.exception.*;
import fr.insee.pearljam.infrastructure.campaign.entity.VisibilityDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CampaignServiceImplTest {

    private CampaignFakeRepository campaignRepository;
    private VisibilityFakeService visibilityService;
    private CampaignServiceImpl campaignService;
    private final OrganizationUnit existingOrganizationUnit = new OrganizationUnit("OU-NORTH", "label-ou", OrganizationUnitType.LOCAL);
    private final Campaign existingCampaign =  new Campaign(
            "CAMPAIGN-ID",
            "label-campaign",
            IdentificationConfiguration.IASCO,
            ContactOutcomeConfiguration.F2F,
            ContactAttemptConfiguration.F2F,
            "email@email.com",
            true);

    private final Visibility existingVisibility1 =
            new Visibility(existingCampaign.getId(), existingOrganizationUnit.getId(), 1721683250000L,
                    1721683251000L, 1721683252000L,
                    1721683253000L, 1721683254000L, 1721683255000L);
    private final Visibility existingVisibility2 =
            new Visibility(existingCampaign.getId(), "OU2", 1721683250000L,
                    1721683251000L, 1721683252000L,
                    1721683253000L, 1721683254000L, 1721683255000L);

    @BeforeEach
    void setup() {
        campaignRepository = new CampaignFakeRepository();
        campaignRepository.addCampaign(existingCampaign);
        // add referents
        existingCampaign.setReferents(new ArrayList<>());

        visibilityService = new VisibilityFakeService();
        visibilityService.save(existingVisibility1);
        visibilityService.save(existingVisibility2);

        List<VisibilityDB> existingCampaignVisibilities = new ArrayList<>();
        existingCampaignVisibilities
                .add(VisibilityDB.fromModel(existingVisibility1, existingCampaign, existingOrganizationUnit));
        existingCampaignVisibilities
                .add(VisibilityDB.fromModel(existingVisibility2, existingCampaign, existingOrganizationUnit));
        existingCampaign.setVisibilities(existingCampaignVisibilities);

        UserFakeRepository userRepository = new UserFakeRepository();
        SurveyUnitFakeRepository surveyUnitRepository = new SurveyUnitFakeRepository();
        OrganizationUnitFakeRepository organizationUnitRepository = new OrganizationUnitFakeRepository();
        organizationUnitRepository.setOrganizationUnits(List.of(existingOrganizationUnit));
        MessageFakeRepository messageRepository = new MessageFakeRepository();
        UserFakeService userService = new UserFakeService();
        UtilsFakeService utilsService = new UtilsFakeService();
        SurveyUnitFakeService surveyUnitService = new SurveyUnitFakeService();
        PreferenceFakeService preferenceService = new PreferenceFakeService();
        ReferentFakeService referentService = new ReferentFakeService();

        campaignService = new CampaignServiceImpl(
                campaignRepository, userRepository, surveyUnitRepository, organizationUnitRepository, messageRepository,
                userService, utilsService, surveyUnitService, preferenceService, referentService, visibilityService);
    }

    // TODO : handle referent
    @Test
    @DisplayName("Should create a new campaign successfully")
    void shouldCreateNewCampaign() throws CampaignAlreadyExistException, OrganizationalUnitNotFoundException, VisibilityHasInvalidDatesException {
        String campaignId = "CAMP1";
        VisibilityCampaignCreateDto visibilityDto = new VisibilityCampaignCreateDto(1721683250000L, 1721683251000L, 1721683252000L,
                1721683253000L, 1721683254000L, 1721683255000L, existingOrganizationUnit.getId());
        CampaignCreateDto campaignCreateDto = new CampaignCreateDto(
                campaignId,
                "Campaign 1",
                List.of(visibilityDto),
                null,
                null,
                IdentificationConfiguration.IASCO,
                ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true
        );

        campaignService.createCampaign(campaignCreateDto);

        Campaign createdCampaign = campaignRepository.getSavedCampaign();
        assertThat(createdCampaign.getId()).isEqualTo(campaignCreateDto.campaign());
        assertThat(createdCampaign.getLabel()).isEqualTo(campaignCreateDto.campaignLabel());
        assertThat(createdCampaign.getEmail()).isEqualTo(campaignCreateDto.email());
        assertThat(createdCampaign.getCommunicationConfiguration()).isEqualTo(campaignCreateDto.communicationRequestConfiguration());
        assertThat(createdCampaign.getIdentificationConfiguration()).isEqualTo(campaignCreateDto.identificationConfiguration());
        assertThat(createdCampaign.getContactAttemptConfiguration()).isEqualTo(campaignCreateDto.contactAttemptConfiguration());
        assertThat(createdCampaign.getContactOutcomeConfiguration()).isEqualTo(campaignCreateDto.contactOutcomeConfiguration());
        assertThat(createdCampaign.getVisibilities())
                .hasSize(1)
                .satisfiesExactly(visibility -> assertThat(VisibilityDB.toModel(visibility)).isEqualTo(VisibilityCampaignCreateDto.toModel(visibilityDto, campaignId)));
    }

    @Test
    @DisplayName("Should throw CampaignAlreadyExistException when creating a campaign that already exists")
    void shouldThrowCampaignAlreadyExistExceptionWhenCreatingExistingCampaign() {
        String campaignId = existingCampaign.getId();
        VisibilityCampaignCreateDto visibilityDto = new VisibilityCampaignCreateDto(1721683250000L, 1721683251000L, 1721683252000L,
                1721683253000L, 1721683254000L, 1721683255000L, existingOrganizationUnit.getId());
        CampaignCreateDto existingCampaignDto = new CampaignCreateDto(
                campaignId,
                "Existing campaign",
                List.of(visibilityDto),
                null,
                null,
                IdentificationConfiguration.IASCO,
                ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                true
        );

        assertThatThrownBy(() -> campaignService.createCampaign(existingCampaignDto))
                .isInstanceOf(CampaignAlreadyExistException.class)
                .hasMessage(CampaignAlreadyExistException.MESSAGE);
    }

    // TODO : handle referents
    @Test
    @DisplayName("Should update an existing campaign successfully")
    void shouldUpdateExistingCampaign() throws CampaignNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException {
        String campaignId = existingCampaign.getId();

        // Given
        VisibilityCampaignUpdateDto visibilityDto = new VisibilityCampaignUpdateDto(1721683250000L, 1721683251000L, 1721683252000L,
                1721683253000L, 1721683254000L, 1721683255000L, existingOrganizationUnit.getId());

        CampaignUpdateDto updateDto = new CampaignUpdateDto("campaign to update",
                List.of(visibilityDto),
                null, "emailUpdated@email.com",
                IdentificationConfiguration.NOIDENT,
                ContactOutcomeConfiguration.TEL,
                ContactAttemptConfiguration.TEL,
                false);

        // When
        campaignService.updateCampaign(campaignId, updateDto);

        // Then
        Campaign updatedCampaign = campaignRepository.getSavedCampaign();
        assertThat(updatedCampaign.getId()).isEqualTo(campaignId);
        assertThat(updatedCampaign.getLabel()).isEqualTo(updateDto.campaignLabel());
        assertThat(updatedCampaign.getEmail()).isEqualTo(updateDto.email());
        assertThat(updatedCampaign.getCommunicationConfiguration()).isEqualTo(updateDto.communicationRequestConfiguration());
        assertThat(updatedCampaign.getIdentificationConfiguration()).isEqualTo(updateDto.identificationConfiguration());
        assertThat(updatedCampaign.getContactAttemptConfiguration()).isEqualTo(updateDto.contactAttemptConfiguration());
        assertThat(updatedCampaign.getContactOutcomeConfiguration()).isEqualTo(updateDto.contactOutcomeConfiguration());
        assertThat(updatedCampaign.getVisibilities())
                .hasSize(2)
                .anySatisfy(visibilityDB ->
                        assertThat(VisibilityDB.toModel(visibilityDB)).isEqualTo(VisibilityCampaignUpdateDto.toModel(visibilityDto, campaignId)))
                .anySatisfy(visibilityDB ->
                        assertThat(VisibilityDB.toModel(visibilityDB)).isEqualTo(existingVisibility1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    @NullSource
    @DisplayName("Should not update email if empty")
    void shouldNotUpdateEmailIfNull(String emailToUpdate) throws CampaignNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException {
        String campaignId = existingCampaign.getId();

        // Given

        CampaignUpdateDto updateDto = new CampaignUpdateDto("campaign to update",
                null,
                null, emailToUpdate,
                IdentificationConfiguration.NOIDENT,
                ContactOutcomeConfiguration.TEL,
                ContactAttemptConfiguration.TEL,
                false);

        // When
        campaignService.updateCampaign(campaignId, updateDto);

        // Then
        Campaign updatedCampaign = campaignRepository.getSavedCampaign();
        assertThat(updatedCampaign.getEmail()).isEqualTo(existingCampaign.getEmail());
    }

    @Test
    @DisplayName("Should not update visibilities if null")
    void shouldNotUpdateVisibilitiesIfNull() throws CampaignNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException {
        String campaignId = existingCampaign.getId();

        // Given

        CampaignUpdateDto updateDto = new CampaignUpdateDto("campaign to update",
                null,
                null, null,
                IdentificationConfiguration.NOIDENT,
                ContactOutcomeConfiguration.TEL,
                ContactAttemptConfiguration.TEL,
                false);

        // When
        campaignService.updateCampaign(campaignId, updateDto);

        // Then
        Campaign updatedCampaign = campaignRepository.getSavedCampaign();
        assertThat(updatedCampaign.getVisibilities()).hasSize(2);
    }

    // TODO : handle referents
    @Test
    @DisplayName("Should not update referents if null")
    void shouldNotUpdateReferentsIfNull() throws VisibilityHasInvalidDatesException, CampaignNotFoundException, VisibilityNotFoundException {
        String campaignId = existingCampaign.getId();

        // Given
        CampaignUpdateDto updateDto = new CampaignUpdateDto("campaign to update",
                null,
                null, null,
                IdentificationConfiguration.NOIDENT,
                ContactOutcomeConfiguration.TEL,
                ContactAttemptConfiguration.TEL,
                false);

        // When
        campaignService.updateCampaign(campaignId, updateDto);

        // Then
        Campaign updatedCampaign = campaignRepository.getSavedCampaign();
        assertThat(updatedCampaign.getReferents()).containsAll(existingCampaign.getReferents());
    }

    @Test
    @DisplayName("Should throw CampaignNotFoundException when updating a non-existent campaign")
    void shouldThrowCampaignNotFoundExceptionWhenUpdatingNonExistentCampaign() {
        String campaignId = "invalid-campaign";

        CampaignUpdateDto updateDto = new CampaignUpdateDto("campaign to update",
                null,
                null, null,
                IdentificationConfiguration.NOIDENT,
                ContactOutcomeConfiguration.TEL,
                ContactAttemptConfiguration.TEL,
                false);

        assertThatThrownBy(() -> campaignService.updateCampaign(campaignId, updateDto))
                .isInstanceOf(CampaignNotFoundException.class);
    }

    @Test
    @DisplayName("Should return true if the campaign is ongoing")
    void shouldReturnTrueIfCampaignIsOngoing() throws CampaignNotFoundException {
        Visibility ongoingVisibility = new Visibility(existingCampaign.getId(), existingOrganizationUnit.getId(),
                1627845600000L, 1627932000000L,
                1628018400000L, 1628104800000L,
                1628191200000L, Instant.now().plusSeconds(10000).toEpochMilli());
        Visibility closedVisibility = new Visibility(existingCampaign.getId(), existingOrganizationUnit.getId(),
                1627845600000L, 1627932000000L,
                1628018400000L, 1628104800000L,
                1628191200000L, Instant.now().toEpochMilli());

        visibilityService.save(ongoingVisibility);
        visibilityService.save(closedVisibility);
        boolean isOngoing = campaignService.isCampaignOngoing(existingCampaign.getId());

        assertThat(isOngoing).isTrue();
    }

    @Test
    @DisplayName("Should return false if the campaign is not ongoing")
    void shouldReturnFalseIfCampaignIsNotOngoing() throws CampaignNotFoundException {
        Visibility closedVisibility1 = new Visibility(existingCampaign.getId(), existingOrganizationUnit.getId(),
                1627845600000L, 1627932000000L,
                1628018400000L, 1628104800000L,
                1628191200000L, Instant.now().minusSeconds(3600).toEpochMilli());
        Visibility closedVisibility2 = new Visibility(existingCampaign.getId(), existingOrganizationUnit.getId(),
                1627845600000L, 1627932000000L,
                1628018400000L, 1628104800000L,
                1628191200000L, Instant.now().toEpochMilli());

        visibilityService.save(closedVisibility1);
        visibilityService.save(closedVisibility2);
        boolean isOngoing = campaignService.isCampaignOngoing(existingCampaign.getId());
        assertThat(isOngoing).isFalse();
    }

    @Test
    @DisplayName("Should throw CampaignNotFoundException when checking if a non-existent campaign is ongoing")
    void shouldThrowCampaignNotFoundExceptionWhenCheckingIfNonExistentCampaignIsOngoing() {
        String campaignId = "notfound-campaign";
        assertThatThrownBy(() -> campaignService.isCampaignOngoing(campaignId))
                .isInstanceOf(CampaignNotFoundException.class);
    }
}
