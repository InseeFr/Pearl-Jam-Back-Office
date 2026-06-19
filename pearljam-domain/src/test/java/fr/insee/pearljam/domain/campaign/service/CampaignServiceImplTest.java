package fr.insee.pearljam.domain.campaign.service;

import fr.insee.pearljam.contracts.campaign.dto.input.*;
import fr.insee.pearljam.domain.campaign.CampaignModel;
import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.service.dummy.*;
import fr.insee.pearljam.domain.campaign.service.exception.*;
import fr.insee.pearljam.domain.campaign.service.model.Visibility;
import fr.insee.pearljam.domain.campaign.stub.CampaignVisibilityPortStub;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnitType;
import fr.insee.pearljam.domain.reporting.readmodel.progress.CampaignPhase;
import fr.insee.pearljam.domain.surveyunit.service.dummy.SurveyUnitFakeService;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.VisibilityDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService.FIXED_TIMESTAMP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CampaignServiceImplTest {

    private CampaignFakeRepository campaignRepository;
    private VisibilityFakeService visibilityService;
    private CampaignServiceImpl campaignService;
    private CampaignVisibilityPortStub campaignVisibilityPortStub;
    private final DateService dateService = new FixedDateService();
    private final OrganizationUnitDB existingOrganizationUnit = new OrganizationUnitDB("OU-NORTH", "label-ou", OrganizationUnitType.LOCAL);
    private final CampaignDB existingCampaign = new CampaignDB(
            "CAMPAIGN-ID",
            "label-campaign",
            IdentificationConfiguration.HOUSEF2F,
            ContactOutcomeConfiguration.F2F,
            ContactAttemptConfiguration.F2F,
            "email@email.com",
            false,
            false);

    private final Visibility existingVisibility1 =
            new Visibility(existingCampaign.getId(), existingOrganizationUnit.getId(), 1721683250000L,
                    1721683251000L, 1721683252000L,
                    1721683253000L, 1721683254000L, 1721683255000L,
                    true, "mail1", "tel1");
    private final Visibility existingVisibility2 =
            new Visibility(existingCampaign.getId(), "OU2", 1721683250000L,
                    1721683251000L, 1721683252000L,
                    1721683253000L, 1721683254000L, 1721683255000L,
                    true, "mail2", "tel2");

    @BeforeEach
    void setup() {
        campaignRepository = new CampaignFakeRepository();
        campaignRepository.addCampaign(existingCampaign);
        existingCampaign.setReferents(new ArrayList<>());

        visibilityService = new VisibilityFakeService();
        visibilityService.save(existingVisibility1);
        visibilityService.save(existingVisibility2);

        List<VisibilityDB> existingCampaignVisibilities = new ArrayList<>();
        existingCampaignVisibilities.add(VisibilityDB.fromModel(existingVisibility1, existingCampaign, existingOrganizationUnit));
        existingCampaignVisibilities.add(VisibilityDB.fromModel(existingVisibility2, existingCampaign, existingOrganizationUnit));
        existingCampaign.setVisibilities(existingCampaignVisibilities);

        UserFakeRepository userRepository = new UserFakeRepository();
        SurveyUnitRepositoryStub surveyUnitRepository = new SurveyUnitRepositoryStub();
        OrganizationUnitFakeRepository organizationUnitRepository = new OrganizationUnitFakeRepository();
        organizationUnitRepository.setOrganizationUnits(List.of(existingOrganizationUnit));
        MessageFakeRepository messageRepository = new MessageFakeRepository();
        UserFakeService userService = new UserFakeService();
        SurveyUnitFakeService surveyUnitService = new SurveyUnitFakeService();
        PreferenceFakeService preferenceService = new PreferenceFakeService();
        ReferentFakeService referentService = new ReferentFakeService();
        ReferentFakeRepository referentRepository = new ReferentFakeRepository();
        InterviewerCountFakeRepository interviewerCountRepository = new InterviewerCountFakeRepository();
        SurveyUnitCountFakeService surveyUnitCountService = new SurveyUnitCountFakeService();
        campaignVisibilityPortStub = new CampaignVisibilityPortStub(List.of());

        campaignService = Mockito.spy(new CampaignServiceImpl(
                campaignRepository, userRepository, surveyUnitRepository, organizationUnitRepository, messageRepository,
                userService, surveyUnitService, preferenceService, referentService, referentRepository, visibilityService, campaignVisibilityPortStub, dateService, interviewerCountRepository, surveyUnitCountService));
    }

    // TODO : handle referent
    @Test
    @DisplayName("Should create a new campaign successfully")
    void shouldCreateNewCampaign() throws CampaignAlreadyExistException, OrganizationalUnitNotFoundException, VisibilityHasInvalidDatesException {
        String campaignId = "SIMPSONS2020X00";

        CommunicationTemplateCreateDto communicationTemplateDto = new CommunicationTemplateCreateDto("meshuggahId", CommunicationMedium.EMAIL, CommunicationType.NOTICE);
        VisibilityCampaignCreateDto visibilityDto = new VisibilityCampaignCreateDto(1721683250000L, 1721683251000L, 1721683252000L,
                1721683253000L, 1721683254000L, 1721683255000L, existingOrganizationUnit.getId(),
                true, "mail", "tel");
        CampaignCreateDto campaignCreateDto = new CampaignCreateDto(
                campaignId,
                "Campaign 1",
                List.of(visibilityDto),
                List.of(communicationTemplateDto),
                null,
                null,
                IdentificationConfiguration.HOUSEF2F,
                ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                false,
                false
        );

        campaignService.createCampaign(campaignCreateDto);

        CampaignDB createdCampaign = campaignRepository.getSavedCampaign();
        assertThat(createdCampaign.getId()).isEqualTo(campaignCreateDto.campaign());
        assertThat(createdCampaign.getLabel()).isEqualTo(campaignCreateDto.campaignLabel());
        assertThat(createdCampaign.getEmail()).isEqualTo(campaignCreateDto.email());
        assertThat(createdCampaign.getIdentificationConfiguration()).isEqualTo(campaignCreateDto.identificationConfiguration());
        assertThat(createdCampaign.getContactAttemptConfiguration()).isEqualTo(campaignCreateDto.contactAttemptConfiguration());
        assertThat(createdCampaign.getContactOutcomeConfiguration()).isEqualTo(campaignCreateDto.contactOutcomeConfiguration());
        assertThat(createdCampaign.getVisibilities())
                .hasSize(1)
                .satisfiesExactly(visibility -> assertThat(VisibilityDB.toModel(visibility))
                        .isEqualTo(VisibilityCampaignCreateDto.toModel(visibilityDto, campaignId)));
        assertThat(createdCampaign.getCommunicationTemplates())
                .hasSize(1)
                .satisfiesExactly(communicationTemplateDB ->
                        assertThat(CommunicationTemplateDB.toModel(communicationTemplateDB))
                                .isEqualTo(CommunicationTemplateCreateDto.toModel(communicationTemplateDto, campaignId)));
    }

    @Test
    @DisplayName("Should throw CampaignAlreadyExistException when creating a campaign that already exists")
    void shouldThrowCampaignAlreadyExistExceptionWhenCreatingExistingCampaign() {
        String campaignId = existingCampaign.getId();
        VisibilityCampaignCreateDto visibilityDto = new VisibilityCampaignCreateDto(1721683250000L, 1721683251000L, 1721683252000L,
                1721683253000L, 1721683254000L, 1721683255000L, existingOrganizationUnit.getId(),
                true, "mail", "tel");
        CampaignCreateDto existingCampaignDto = new CampaignCreateDto(
                campaignId,
                "Existing campaign",
                List.of(visibilityDto),
                null,
                null,
                null,
                IdentificationConfiguration.HOUSEF2F,
                ContactOutcomeConfiguration.F2F,
                ContactAttemptConfiguration.F2F,
                false,
                false
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

        VisibilityCampaignUpdateDto visibilityDto = new VisibilityCampaignUpdateDto(1721683250000L, 1721683251000L, 1721683252000L,
                1721683253000L, 1721683254000L, 1721683255000L, existingOrganizationUnit.getId(),
                true, "mail1", "tel1");
        CampaignUpdateDto updateDto = new CampaignUpdateDto("campaign to update",
                List.of(visibilityDto),
                List.of(),
                "emailUpdated@email.com",
                ContactOutcomeConfiguration.TEL,
                ContactAttemptConfiguration.TEL,
                false);

        campaignService.updateCampaign(campaignId, updateDto);

        CampaignDB updatedCampaign = campaignRepository.getSavedCampaign();
        assertThat(updatedCampaign.getId()).isEqualTo(campaignId);
        assertThat(updatedCampaign.getLabel()).isEqualTo(updateDto.campaignLabel());
        assertThat(updatedCampaign.getEmail()).isEqualTo(updateDto.email());
        assertThat(updatedCampaign.getIdentificationConfiguration()).isEqualTo(existingCampaign.getIdentificationConfiguration());
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
        CampaignUpdateDto updateDto = new CampaignUpdateDto("campaign to update",
                null,
                null,
                emailToUpdate,
                ContactOutcomeConfiguration.TEL,
                ContactAttemptConfiguration.TEL,
                false);

        campaignService.updateCampaign(existingCampaign.getId(), updateDto);

        assertThat(campaignRepository.getSavedCampaign().getEmail()).isEqualTo(existingCampaign.getEmail());
    }

    @Test
    @DisplayName("Should not update visibilities if null")
    void shouldNotUpdateVisibilitiesIfNull() throws CampaignNotFoundException, VisibilityNotFoundException, VisibilityHasInvalidDatesException {
        CampaignUpdateDto updateDto = new CampaignUpdateDto("campaign to update",
                null,
                null,
                null,
                ContactOutcomeConfiguration.TEL,
                ContactAttemptConfiguration.TEL,
                false);

        campaignService.updateCampaign(existingCampaign.getId(), updateDto);

        assertThat(campaignRepository.getSavedCampaign().getVisibilities()).hasSize(2);
    }

    @Test
    @DisplayName("Should not update referents if null")
    void shouldNotUpdateReferentsIfNull() throws VisibilityHasInvalidDatesException, CampaignNotFoundException, VisibilityNotFoundException {
        CampaignUpdateDto updateDto = new CampaignUpdateDto("campaign to update",
                null,
                null,
                null,
                ContactOutcomeConfiguration.TEL,
                ContactAttemptConfiguration.TEL,
                false);

        campaignService.updateCampaign(existingCampaign.getId(), updateDto);

        assertThat(campaignRepository.getSavedCampaign().getReferents()).containsAll(existingCampaign.getReferents());
    }

    @Test
    @DisplayName("Should throw CampaignNotFoundException when updating a non-existent campaign")
    void shouldThrowCampaignNotFoundExceptionWhenUpdatingNonExistentCampaign() {
        CampaignUpdateDto updateDto = new CampaignUpdateDto("campaign to update",
                null,
                null,
                null,
                ContactOutcomeConfiguration.TEL,
                ContactAttemptConfiguration.TEL,
                false);

        assertThatThrownBy(() -> campaignService.updateCampaign("invalid-campaign", updateDto))
                .isInstanceOf(CampaignNotFoundException.class);
    }

    @Test
    @DisplayName("Should return true if the campaign is ongoing")
    void shouldReturnTrueIfCampaignIsOngoing() throws CampaignNotFoundException {
        Visibility ongoingVisibility = new Visibility(existingCampaign.getId(), existingOrganizationUnit.getId(),
                1627845600000L, 1627932000000L,
                1628018400000L, 1628104800000L,
                1628191200000L, FIXED_TIMESTAMP + 10000,
                true, "mail", "tel");
        Visibility closedVisibility = new Visibility(existingCampaign.getId(), existingOrganizationUnit.getId(),
                1627845600000L, 1627932000000L,
                1628018400000L, 1628104800000L,
                1628191200000L, FIXED_TIMESTAMP - 1,
                true, "mail", "tel");

        visibilityService.save(ongoingVisibility);
        visibilityService.save(closedVisibility);

        assertThat(campaignService.isCampaignOngoing(existingCampaign.getId())).isTrue();
    }

    @Test
    @DisplayName("Should return false if the campaign is not ongoing")
    void shouldReturnFalseIfCampaignIsNotOngoing() throws CampaignNotFoundException {
        Visibility closedVisibility1 = new Visibility(existingCampaign.getId(), existingOrganizationUnit.getId(),
                1627845600000L, 1627932000000L,
                1628018400000L, 1628104800000L,
                1628191200000L, FIXED_TIMESTAMP - 3600000,
                true, "mail", "tel");
        Visibility closedVisibility2 = new Visibility(existingCampaign.getId(), existingOrganizationUnit.getId(),
                1627845600000L, 1627932000000L,
                1628018400000L, 1628104800000L,
                1628191200000L, FIXED_TIMESTAMP - 1,
                true, "mail", "tel");

        visibilityService.save(closedVisibility1);
        visibilityService.save(closedVisibility2);

        assertThat(campaignService.isCampaignOngoing(existingCampaign.getId())).isFalse();
    }

    @Test
    @DisplayName("Should throw CampaignNotFoundException when checking if a non-existent campaign is ongoing")
    void shouldThrowCampaignNotFoundExceptionWhenCheckingIfNonExistentCampaignIsOngoing() {
        assertThatThrownBy(() -> campaignService.isCampaignOngoing("notfound-campaign"))
                .isInstanceOf(CampaignNotFoundException.class);
    }

    @Test
    @DisplayName("Should return campaigns in the requested phase")
    void shouldReturnCampaignPreferencesForSpecificPhase() {

        CampaignVisibility campaignVisibility = new CampaignVisibility(
            existingCampaign.getId(),
            existingCampaign.getLabel(),
            "test@email.com",
            FIXED_TIMESTAMP - 700000000L, // managementStartDate
            FIXED_TIMESTAMP - 600000000L, // interviewerStartDate
            FIXED_TIMESTAMP - 500000000L, // identificationPhaseStartDate
            FIXED_TIMESTAMP - 200000000L, // collectionStartDate
            FIXED_TIMESTAMP + 100000000L, // collectionEndDate
            FIXED_TIMESTAMP + 200000000L  // endDate
        );

        campaignVisibilityPortStub.setCampaignsWithVisibility(List.of(campaignVisibility));
        List<CampaignModel> result =
            campaignService.getUserCampaignsForSpecificPhase(
                "test-user",
                CampaignPhase.COLLECTION_IN_PROGRESS);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(existingCampaign.getId());
        assertThat(result.getFirst().label()).isEqualTo(existingCampaign.getLabel());
    }

    @Test
    @DisplayName("Should return empty list when no campaigns match the specified phase")
    void shouldReturnEmptyListWhenNoCampaignsMatchPhase() {

        CampaignVisibility campaignVisibility = new CampaignVisibility(
            existingCampaign.getId(),
            existingCampaign.getLabel(),
            "test@email.com",
            FIXED_TIMESTAMP - 700000000L,
            FIXED_TIMESTAMP - 600000000L,
            FIXED_TIMESTAMP - 500000000L,
            FIXED_TIMESTAMP - 200000000L,
            FIXED_TIMESTAMP - 100000000L,
            FIXED_TIMESTAMP + 200000000L
        );

        campaignVisibilityPortStub.setCampaignsWithVisibility(List.of(campaignVisibility));

        List<CampaignModel> result =
            campaignService.getUserCampaignsForSpecificPhase(
                "test-user",
                CampaignPhase.COLLECTION_IN_PROGRESS);

        assertThat(result).isEmpty();
    }



        @Test
        @DisplayName("Should filter campaigns by phase and return only matching ones")
        void shouldFilterCampaignsByPhase() {
            // Given
            CampaignVisibility managementPhaseCampaign = new CampaignVisibility(
                    "CAMP-MGMT", "Management Campaign", "mgmt@test.com",
                    FIXED_TIMESTAMP - 800000000L, // managementStartDate (past)
                    FIXED_TIMESTAMP - 700000000L, // interviewerStartDate
                    FIXED_TIMESTAMP - 600000000L, // identificationPhaseStartDate
                    FIXED_TIMESTAMP + 100000000L, // collectionStartDate (FUTUR -> INITIAL_ASSIGNMENT)
                    FIXED_TIMESTAMP + 200000000L, // collectionEndDate
                    FIXED_TIMESTAMP + 300000000L  // endDate
            );

            CampaignVisibility collectionPhaseCampaign = new CampaignVisibility(
                    "CAMP-COLL", "Collection Campaign", "coll@test.com",
                    FIXED_TIMESTAMP - 700000000L, // managementStartDate (past)
                    FIXED_TIMESTAMP - 600000000L, // interviewerStartDate
                    FIXED_TIMESTAMP - 500000000L, // identificationPhaseStartDate
                    FIXED_TIMESTAMP - 200000000L, // collectionStartDate (past)
                    FIXED_TIMESTAMP + 100000000L, // collectionEndDate (future -> COLLECTION_IN_PROGRESS)
                    FIXED_TIMESTAMP + 200000000L  // endDate
            );

            CampaignVisibility closedPhaseCampaign = new CampaignVisibility(
                    "CAMP-CLOSED", "Closed Campaign", "closed@test.com",
                    FIXED_TIMESTAMP - 900000000L, // managementStartDate (past)
                    FIXED_TIMESTAMP - 800000000L, // interviewerStartDate
                    FIXED_TIMESTAMP - 700000000L, // identificationPhaseStartDate
                    FIXED_TIMESTAMP - 600000000L, // collectionStartDate (past)
                    FIXED_TIMESTAMP - 500000000L, // collectionEndDate (past)
                    FIXED_TIMESTAMP + 100000000L  // endDate (future -> COLLECTION_COMPLETED)
            );

            campaignVisibilityPortStub.setCampaignsWithVisibility(
                    List.of(managementPhaseCampaign, collectionPhaseCampaign, closedPhaseCampaign));

            // When
            List<CampaignModel> result = campaignService.getUserCampaignsForSpecificPhase(
                    "test-user", CampaignPhase.COLLECTION_IN_PROGRESS);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().id()).isEqualTo("CAMP-COLL");
            assertThat(result.getFirst().label()).isEqualTo("Collection Campaign");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "COLLECTION_IN_PROGRESS",
                "COLLECTION_COMPLETED",
                "INITIAL_ASSIGNMENT"
        })
        @DisplayName("Should handle all campaign phases correctly")
        void shouldHandleAllCampaignPhasesCorrectly(String phaseName) {
            // Given
            CampaignPhase phase = CampaignPhase.valueOf(phaseName);
            CampaignVisibility campaignInPhase = createCampaignVisibilityForPhase(phase);

            campaignVisibilityPortStub.setCampaignsWithVisibility(List.of(campaignInPhase));

            // When
            List<CampaignModel> result = campaignService.getUserCampaignsForSpecificPhase(
                    "test-user", phase);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().id()).isEqualTo(campaignInPhase.id());
        }

        @Test
        @DisplayName("Should return empty list when user has no organization units")
        void shouldReturnEmptyListWhenUserHasNoOrganizationUnits() {
            // Given
            UserFakeService userServiceWithNoOUs = Mockito.spy(new UserFakeService());
            CampaignServiceImpl serviceWithNoOUs = new CampaignServiceImpl(
                    campaignRepository, 
                    new UserFakeRepository(), 
                    new SurveyUnitRepositoryStub(),
                    new OrganizationUnitFakeRepository(),
                    new MessageFakeRepository(),
                    userServiceWithNoOUs,
                    new SurveyUnitFakeService(),
                    new PreferenceFakeService(),
                    new ReferentFakeService(),
                    new ReferentFakeRepository(),
                    visibilityService,
                    campaignVisibilityPortStub,
                    dateService,
                    new InterviewerCountFakeRepository(),
                    new SurveyUnitCountFakeService());

            Mockito.when(userServiceWithNoOUs.getUserOUs("userNoOU", true)).thenReturn(List.of());

            // When
            List<CampaignModel> result = serviceWithNoOUs.getUserCampaignsForSpecificPhase(
                    "userNoOU", CampaignPhase.COLLECTION_IN_PROGRESS);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should map CampaignVisibility to CampaignModel with id and label")
        void shouldMapCampaignVisibilityToCampaignModel() {
            // Given
            CampaignVisibility visibility = new CampaignVisibility(
                    "TEST-ID", "Test Label", "test@email.com",
                    FIXED_TIMESTAMP - 200000000L,
                    FIXED_TIMESTAMP - 100000000L,
                    FIXED_TIMESTAMP - 50000000L,
                    FIXED_TIMESTAMP - 10000000L,
                    FIXED_TIMESTAMP + 100000000L,
                    FIXED_TIMESTAMP + 200000000L
            );

            campaignVisibilityPortStub.setCampaignsWithVisibility(List.of(visibility));

            // When
            List<CampaignModel> result = campaignService.getUserCampaignsForSpecificPhase(
                    "test-user", CampaignPhase.COLLECTION_IN_PROGRESS);

            // Then
            assertThat(result).hasSize(1);
            CampaignModel model = result.getFirst();
            assertThat(model.id()).isEqualTo("TEST-ID");
            assertThat(model.label()).isEqualTo("Test Label");
        }

        private CampaignVisibility createCampaignVisibilityForPhase(CampaignPhase phase) {
            return switch (phase) {
                case INITIAL_ASSIGNMENT -> new CampaignVisibility(
                        "CAMP-" + phase, "Campaign " + phase, "test@email.com",
                        FIXED_TIMESTAMP - 200000000L,
                        FIXED_TIMESTAMP - 100000000L,
                        FIXED_TIMESTAMP - 1000000L,
                        FIXED_TIMESTAMP + 100000000L,
                        FIXED_TIMESTAMP + 200000000L,
                        FIXED_TIMESTAMP + 300000000L
                );
                case COLLECTION_IN_PROGRESS -> new CampaignVisibility(
                        "CAMP-" + phase, "Campaign " + phase, "test@email.com",
                        FIXED_TIMESTAMP - 300000000L,
                        FIXED_TIMESTAMP - 200000000L,
                        FIXED_TIMESTAMP - 100000000L,
                        FIXED_TIMESTAMP - 1000000L,
                        FIXED_TIMESTAMP + 100000000L,
                        FIXED_TIMESTAMP + 200000000L
                );
                case COLLECTION_COMPLETED -> new CampaignVisibility(
                        "CAMP-" + phase, "Campaign " + phase, "test@email.com",
                        FIXED_TIMESTAMP - 400000000L,
                        FIXED_TIMESTAMP - 300000000L,
                        FIXED_TIMESTAMP - 200000000L,
                        FIXED_TIMESTAMP - 1000000L,
                        FIXED_TIMESTAMP - 500000L,
                        FIXED_TIMESTAMP + 100000000L
                );
            };

    }
}