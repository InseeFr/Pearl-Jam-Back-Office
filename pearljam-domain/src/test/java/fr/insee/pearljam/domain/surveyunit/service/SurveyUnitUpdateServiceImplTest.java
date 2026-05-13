package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.surveyunit.dto.contacthistory.NextContactHistoryDto;
import fr.insee.pearljam.contracts.surveyunit.dto.person.PersonDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.CommentDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.CommunicationRequestCreateDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.ContactOutcomeDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.SurveyUnitUpdateDto;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.in.VisibilityService;
import fr.insee.pearljam.domain.campaign.service.exception.CommunicationTemplateNotFoundException;
import fr.insee.pearljam.domain.campaign.service.exception.VisibilityNotFoundException;
import fr.insee.pearljam.domain.campaign.service.model.Visibility;
import fr.insee.pearljam.domain.organizationunit.model.OrganizationUnitType;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import fr.insee.pearljam.domain.surveyunit.model.Title;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestReason;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcome;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.service.dummy.CommunicationRequestFakeRepository;
import fr.insee.pearljam.domain.surveyunit.service.dummy.CommunicationTemplateFakeRepository;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.identification.HouseF2FIdentificationDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SurveyUnitUpdateServiceImplTest {

    private CommunicationRequestFakeRepository communicationRequestRepository;
    private CommunicationTemplateFakeRepository communicationTemplateRepository;

    @Mock
    private VisibilityService visibilityService;

    @Mock
    private DateService dateService;

    private SurveyUnitUpdateServiceImpl service;

    private static final String SURVEY_UNIT_ID = "SU-001";
    private static final String CAMPAIGN_ID = "CAMPAIGN-001";
    private static final String OU_ID = "OU-001";
    private static final String COMMENT_VALUE = "Test comment";
    private static final String COMMUNICATION_TEMPLATE_ID = "TEMPLATE-001";
    private static final long CURRENT_TIMESTAMP = 1000000L;
    private static final long CREATION_TIMESTAMP = 500000L;

    @BeforeEach
    void init() {
        communicationRequestRepository = new CommunicationRequestFakeRepository();
        communicationTemplateRepository = new CommunicationTemplateFakeRepository();
        service = new SurveyUnitUpdateServiceImpl(
                communicationRequestRepository,
                communicationTemplateRepository,
                visibilityService,
                dateService
        );
    }

    // ==================== BUILDERS ====================

    private SurveyUnitDB buildTestSurveyUnit() {
        CampaignDB campaign = new CampaignDB(CAMPAIGN_ID, "Test Campaign",
                IdentificationConfiguration.HOUSEF2F, null, null, "test@email.com", false, false);
        OrganizationUnitDB ou = new OrganizationUnitDB(OU_ID, "Test OU", OrganizationUnitType.LOCAL);
        SurveyUnitDB surveyUnit = new SurveyUnitDB();
        surveyUnit.setId(SURVEY_UNIT_ID);
        surveyUnit.setCampaign(campaign);
        surveyUnit.setOrganizationUnit(ou);
        HouseF2FIdentificationDB identification = new HouseF2FIdentificationDB();
        identification.setSurveyUnit(surveyUnit);
        surveyUnit.setIdentification(identification);
        return surveyUnit;
    }

    private SurveyUnitUpdateDto buildUpdateDtoWithAllData() {
        return new SurveyUnitUpdateDto(SURVEY_UNIT_ID, buildPersonDtos(), null, null,
                buildCommentDtos(), null, null, buildContactOutcomeDto(), null,
                buildCommunicationRequestCreateDtos(), buildNextContactHistoryDto());
    }

    private SurveyUnitUpdateDto buildUpdateDtoWithNullComments() {
        return new SurveyUnitUpdateDto(SURVEY_UNIT_ID, buildPersonDtos(), null, null,
                null, null, null, buildContactOutcomeDto(), null,
                buildCommunicationRequestCreateDtos(), buildNextContactHistoryDto());
    }

    private SurveyUnitUpdateDto buildUpdateDtoWithNullCommunicationRequests() {
        return new SurveyUnitUpdateDto(SURVEY_UNIT_ID, buildPersonDtos(), null, null,
                buildCommentDtos(), null, null, buildContactOutcomeDto(), null,
                null, buildNextContactHistoryDto());
    }

    private SurveyUnitUpdateDto buildUpdateDtoWithNullIdentification() {
        return new SurveyUnitUpdateDto(SURVEY_UNIT_ID, buildPersonDtos(), null, null,
                buildCommentDtos(), null, null, buildContactOutcomeDto(), null,
                buildCommunicationRequestCreateDtos(), buildNextContactHistoryDto());
    }

    private SurveyUnitUpdateDto buildUpdateDtoWithNullPersons() {
        return new SurveyUnitUpdateDto(SURVEY_UNIT_ID, null, null, null,
                buildCommentDtos(), null, null, buildContactOutcomeDto(), null,
                buildCommunicationRequestCreateDtos(), buildNextContactHistoryDto());
    }

    private SurveyUnitUpdateDto buildUpdateDtoWithNullContactOutcome() {
        return new SurveyUnitUpdateDto(SURVEY_UNIT_ID, buildPersonDtos(), null, null,
                buildCommentDtos(), null, null, null, null,
                buildCommunicationRequestCreateDtos(), buildNextContactHistoryDto());
    }

    private SurveyUnitUpdateDto buildUpdateDtoWithNullNextContactHistory() {
        return new SurveyUnitUpdateDto(SURVEY_UNIT_ID, buildPersonDtos(), null, null,
                buildCommentDtos(), null, null, buildContactOutcomeDto(), null,
                buildCommunicationRequestCreateDtos(), null);
    }

    private SurveyUnitUpdateDto buildUpdateDtoWithAllNull() {
        return new SurveyUnitUpdateDto(SURVEY_UNIT_ID, null, null, null, null, null, null, null, null, null, null);
    }

    private List<CommentDto> buildCommentDtos() {
        return List.of(new CommentDto(CommentType.INTERVIEWER, COMMENT_VALUE));
    }

    private List<PersonDto> buildPersonDtos() {
        return List.of(new PersonDto(1L, Title.MISTER, "John", "Doe", "john@doe.com", 1990L, false, List.of()));
    }

    private ContactOutcomeDto buildContactOutcomeDto() {
        return new ContactOutcomeDto(System.currentTimeMillis(), ContactOutcomeType.INA, 1);
    }

    private NextContactHistoryDto buildNextContactHistoryDto() {
        return new NextContactHistoryDto(List.of());
    }

    private List<CommunicationRequestCreateDto> buildCommunicationRequestCreateDtos() {
        return List.of(buildCommunicationRequestCreateDto());
    }

    private CommunicationRequestCreateDto buildCommunicationRequestCreateDto() {
        return new CommunicationRequestCreateDto(COMMUNICATION_TEMPLATE_ID, CREATION_TIMESTAMP, CommunicationRequestReason.REFUSAL);
    }

    private CommunicationTemplate buildCommunicationTemplate(CommunicationMedium medium) {
        return new CommunicationTemplate(CAMPAIGN_ID, COMMUNICATION_TEMPLATE_ID, medium, CommunicationType.NOTICE);
    }

    private Visibility buildVisibility(Boolean useLetterCommunication) {
        return new Visibility(CAMPAIGN_ID, OU_ID, 100L, 200L, 300L, 400L, 500L, 600L,
                useLetterCommunication, "test@email.com", "123456");
    }

    // ==================== HELPER METHODS ====================

    private CommunicationRequest invokeGetNewCommunicationRequest(
            CommunicationRequestCreateDto requestDto, SurveyUnitDB surveyUnit, Long readyTimestamp) throws Exception {
        Method method = SurveyUnitUpdateServiceImpl.class
                .getDeclaredMethod("getNewCommunicationRequest",
                        CommunicationRequestCreateDto.class, SurveyUnitDB.class, Long.class);
        method.setAccessible(true);
        return (CommunicationRequest) method.invoke(service, requestDto, surveyUnit, readyTimestamp);
    }

    private ContactOutcome invokeConvertDeprecatedContactOutcomeValue(ContactOutcome contactOutcome) throws Exception {
        Method method = SurveyUnitUpdateServiceImpl.class
                .getDeclaredMethod("convertDeprecatedContactOutcomeValue", ContactOutcome.class);
        method.setAccessible(true);
        return (ContactOutcome) method.invoke(service, contactOutcome);
    }

    // ==================== PHASE 2 - HAPPY PATH TESTS ====================

    @Test
    @DisplayName("When updating survey unit with all data, all fields are updated successfully")
    void testUpdateSurveyUnitInfosAllData() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = buildUpdateDtoWithAllData();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        when(dateService.getCurrentTimestamp()).thenReturn(CURRENT_TIMESTAMP);
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(surveyUnit.getComments()).hasSize(1);
        assertThat(surveyUnit.getPersons()).hasSize(1);
        assertThat(communicationRequestRepository.getCommunicationRequestsAdded()).hasSize(1);
    }

    @Test
    @DisplayName("When creating communication request with EMAIL medium, request is created")
    void testGetNewCommunicationRequestEmail() throws Exception {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        CommunicationRequestCreateDto requestDto = buildCommunicationRequestCreateDto();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        CommunicationRequest result = invokeGetNewCommunicationRequest(requestDto, surveyUnit, CURRENT_TIMESTAMP);
        assertThat(result).isNotNull();
        assertThat(result.campaignId()).isEqualTo(CAMPAIGN_ID);
        assertThat(result.meshuggahId()).isEqualTo(COMMUNICATION_TEMPLATE_ID);
        assertThat(result.status()).hasSize(2);
    }

    @Test
    @DisplayName("When creating communication request with LETTER medium and useLetterCommunication=true, request is created")
    void testGetNewCommunicationRequestLetterUseLetterTrue() throws Exception {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        CommunicationRequestCreateDto requestDto = buildCommunicationRequestCreateDto();        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.LETTER);
        communicationTemplateRepository.save(template);
        Visibility visibility = buildVisibility(true);
        when(visibilityService.findVisibility(CAMPAIGN_ID, OU_ID)).thenReturn(Optional.of(visibility));
        CommunicationRequest result = invokeGetNewCommunicationRequest(requestDto, surveyUnit, CURRENT_TIMESTAMP);
        assertThat(result).isNotNull();
        assertThat(result.status()).allSatisfy(status ->
            assertThat(status.status()).isNotEqualTo(fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationStatusType.CANCELLED));
    }

    @Test
    @DisplayName("When creating communication request with LETTER medium and useLetterCommunication=false, request is cancelled")
    void testGetNewCommunicationRequestLetterUseLetterFalse() throws Exception {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        CommunicationRequestCreateDto requestDto = buildCommunicationRequestCreateDto();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.LETTER);
        communicationTemplateRepository.save(template);
        Visibility visibility = buildVisibility(false);
        when(visibilityService.findVisibility(CAMPAIGN_ID, OU_ID)).thenReturn(Optional.of(visibility));
        CommunicationRequest result = invokeGetNewCommunicationRequest(requestDto, surveyUnit, CURRENT_TIMESTAMP);
        assertThat(result).isNotNull();
        assertThat(result.status()).anySatisfy(status ->
            assertThat(status.status()).isEqualTo(fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationStatusType.CANCELLED));
    }

    // ==================== PHASE 3 - NULL BRANCHES TESTS ====================

    @Test
    @DisplayName("When updating survey unit with null comments, comments are not updated")
    void su02_testUpdateSurveyUnitInfosNullComments() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = buildUpdateDtoWithNullComments();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        when(dateService.getCurrentTimestamp()).thenReturn(CURRENT_TIMESTAMP);
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(surveyUnit.getComments()).isEmpty();
    }

    @Test
    @DisplayName("When updating survey unit with null communicationRequests, no requests are added")
    void su03_testUpdateSurveyUnitInfosNullCommunicationRequests() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = buildUpdateDtoWithNullCommunicationRequests();
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(communicationRequestRepository.getCommunicationRequestsAdded()).isNull();
    }


    @Test
    @DisplayName("When updating survey unit with null persons, persons are set to empty list")
    void su05_testUpdateSurveyUnitInfosNullPersons() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = buildUpdateDtoWithNullPersons();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        when(dateService.getCurrentTimestamp()).thenReturn(CURRENT_TIMESTAMP);
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(surveyUnit.getPersons()).isEmpty();
    }

    @Test
    @DisplayName("When updating survey unit with null contactOutcome, contactOutcome is null")
    void su06_testUpdateSurveyUnitInfosNullContactOutcome() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = buildUpdateDtoWithNullContactOutcome();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        when(dateService.getCurrentTimestamp()).thenReturn(CURRENT_TIMESTAMP);
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(surveyUnit.getContactOutcome()).isNull();
    }

    @Test
    @DisplayName("When updating survey unit with null nextContactHistory, nextContactHistory is not updated")
    void su07_testUpdateSurveyUnitInfosNullNextContactHistory() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = buildUpdateDtoWithNullNextContactHistory();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        when(dateService.getCurrentTimestamp()).thenReturn(CURRENT_TIMESTAMP);
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(surveyUnit.getNextContactHistory()).isNull();
    }

    @Test
    @DisplayName("When updating survey unit with all null fields, no fields are updated")
    void su10_testUpdateSurveyUnitInfosAllNull() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = buildUpdateDtoWithAllNull();
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(surveyUnit.getComments()).isEmpty();
        assertThat(surveyUnit.getPersons()).isEmpty();
        assertThat(communicationRequestRepository.getCommunicationRequestsAdded()).isNull();
        assertThat(surveyUnit.getIdentification()).isNotNull();
        assertThat(surveyUnit.getContactOutcome()).isNull();
        assertThat(surveyUnit.getNextContactHistory()).isNull();
    }

    // ==================== PHASE 4 - EXCEPTIONS + CR-08 TESTS ====================

    @Test
    @DisplayName("When creating communication request with non-existent template, throws CommunicationTemplateNotFoundException")
    void cr04_testGetNewCommunicationRequestTemplateNotFound() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        CommunicationRequestCreateDto requestDto = buildCommunicationRequestCreateDto();
        assertThatThrownBy(() -> invokeGetNewCommunicationRequest(requestDto, surveyUnit, CURRENT_TIMESTAMP))
                .hasCauseInstanceOf(CommunicationTemplateNotFoundException.class);
    }

    @Test
    @DisplayName("When creating LETTER communication request with non-existent visibility, throws VisibilityNotFoundException")
    void cr05_testGetNewCommunicationRequestVisibilityNotFound() throws Exception {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        CommunicationRequestCreateDto requestDto = buildCommunicationRequestCreateDto();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.LETTER);
        communicationTemplateRepository.save(template);
        when(visibilityService.findVisibility(CAMPAIGN_ID, OU_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> invokeGetNewCommunicationRequest(requestDto, surveyUnit, CURRENT_TIMESTAMP))
                .hasCauseInstanceOf(VisibilityNotFoundException.class);
    }

    @Test
    @DisplayName("When creating LETTER communication request with useLetterCommunication=null, request is cancelled")
    void cr08_testGetNewCommunicationRequestLetterUseLetterNull() throws Exception {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        CommunicationRequestCreateDto requestDto = buildCommunicationRequestCreateDto();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.LETTER);
        communicationTemplateRepository.save(template);
        Visibility visibility = buildVisibility(null);
        when(visibilityService.findVisibility(CAMPAIGN_ID, OU_ID)).thenReturn(Optional.of(visibility));
        CommunicationRequest result = invokeGetNewCommunicationRequest(requestDto, surveyUnit, CURRENT_TIMESTAMP);
        assertThat(result).isNotNull();
        assertThat(result.status()).anySatisfy(status ->
            assertThat(status.status()).isEqualTo(fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationStatusType.CANCELLED));
    }

    // ==================== PHASE 5 - EDGE CASES TESTS ====================

    @Test
    @DisplayName("When updating survey unit with empty comments list, comments are set to empty set")
    void su08_testUpdateSurveyUnitInfosEmptyComments() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = new SurveyUnitUpdateDto(SURVEY_UNIT_ID, buildPersonDtos(), null, null,
                List.of(), null, null, buildContactOutcomeDto(), null,
                buildCommunicationRequestCreateDtos(), buildNextContactHistoryDto());
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        when(dateService.getCurrentTimestamp()).thenReturn(CURRENT_TIMESTAMP);
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(surveyUnit.getComments()).isEmpty();
    }

    @Test
    @DisplayName("When updating survey unit with empty persons list, persons are set to empty set")
    void su09_testUpdateSurveyUnitInfosEmptyPersons() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = new SurveyUnitUpdateDto(SURVEY_UNIT_ID, List.of(), null, null,
                buildCommentDtos(), null, null, buildContactOutcomeDto(), null,
                buildCommunicationRequestCreateDtos(), buildNextContactHistoryDto());
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        when(dateService.getCurrentTimestamp()).thenReturn(CURRENT_TIMESTAMP);
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(surveyUnit.getPersons()).isEmpty();
    }

    @Test
    @DisplayName("When creating communication request with creationTs greater than readyTs, timestampDelta >= 0")
    void cr06_testGetNewCommunicationRequestCreationTsGreaterThanReadyTs() throws Exception {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        long creationTs = 2000000L;
        long readyTs = 1000000L;
        CommunicationRequestCreateDto requestDto = new CommunicationRequestCreateDto(
                COMMUNICATION_TEMPLATE_ID, creationTs, CommunicationRequestReason.REFUSAL);
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        CommunicationRequest result = invokeGetNewCommunicationRequest(requestDto, surveyUnit, readyTs);
        assertThat(result).isNotNull();
        assertThat(result.status()).hasSize(2);
        assertThat(result.status().getFirst().date()).isEqualTo(readyTs - 1);
    }

    @Test
    @DisplayName("When creating communication request with creationTs less than readyTs, timestampDelta < 0")
    void cr07_testGetNewCommunicationRequestCreationTsLessThanReadyTs() throws Exception {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        long creationTs = 500000L;
        long readyTs = 1000000L;
        CommunicationRequestCreateDto requestDto = new CommunicationRequestCreateDto(
                COMMUNICATION_TEMPLATE_ID, creationTs, CommunicationRequestReason.REFUSAL);
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        CommunicationRequest result = invokeGetNewCommunicationRequest(requestDto, surveyUnit, readyTs);
        assertThat(result).isNotNull();
        assertThat(result.status()).hasSize(2);
        assertThat(result.status().getFirst().date()).isEqualTo(creationTs);
    }

    // ==================== PHASE 6 - CONVERSIONS + INTEGRATION TESTS ====================

    private static Stream<Arguments> provideContactOutcomeConversionScenarios() {
        return Stream.of(
                Arguments.of(ContactOutcomeType.DCD, ContactOutcomeType.NOA),
                Arguments.of(ContactOutcomeType.DUU, ContactOutcomeType.DUK),
                Arguments.of(ContactOutcomeType.INA, ContactOutcomeType.INA),
                Arguments.of(ContactOutcomeType.REF, ContactOutcomeType.REF),
                Arguments.of(ContactOutcomeType.IMP, ContactOutcomeType.IMP),
                Arguments.of(ContactOutcomeType.UCD, ContactOutcomeType.UCD),
                Arguments.of(ContactOutcomeType.UTR, ContactOutcomeType.UTR),
                Arguments.of(ContactOutcomeType.ALA, ContactOutcomeType.ALA),
                Arguments.of(ContactOutcomeType.DUK, ContactOutcomeType.DUK),
                Arguments.of(ContactOutcomeType.NUH, ContactOutcomeType.NUH),
                Arguments.of(ContactOutcomeType.NOA, ContactOutcomeType.NOA)
        );
    }

    @ParameterizedTest
    @MethodSource("provideContactOutcomeConversionScenarios")
    @DisplayName("When converting deprecated contact outcome value, {0} is converted to {1}")
    void co_testConvertDeprecatedContactOutcomeValue(ContactOutcomeType inputType, ContactOutcomeType expectedType) throws Exception {
        ContactOutcome contactOutcome = new ContactOutcome(1L, System.currentTimeMillis(), inputType, 1, SURVEY_UNIT_ID);
        ContactOutcome result = invokeConvertDeprecatedContactOutcomeValue(contactOutcome);
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(expectedType);
        assertThat(result.id()).isEqualTo(contactOutcome.id());
        assertThat(result.date()).isEqualTo(contactOutcome.date());
        assertThat(result.totalNumberOfContactAttempts()).isEqualTo(contactOutcome.totalNumberOfContactAttempts());
        assertThat(result.surveyUnitId()).isEqualTo(contactOutcome.surveyUnitId());
    }

    @Test
    @DisplayName("On updateSurveyUnitInfos with communicationRequests, verify addCommunicationRequests is called")
    void int01_testAddCommunicationRequestsIsCalled() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = buildUpdateDtoWithAllData();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        when(dateService.getCurrentTimestamp()).thenReturn(CURRENT_TIMESTAMP);
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(communicationRequestRepository.getCommunicationRequestsAdded()).hasSize(1);
    }

    @Test
    @DisplayName("On getNewCommunicationRequest with LETTER medium, verify findVisibility is called")
    void int02_testFindVisibilityIsCalled() throws Exception {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        CommunicationRequestCreateDto requestDto = buildCommunicationRequestCreateDto();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.LETTER);
        communicationTemplateRepository.save(template);
        Visibility visibility = buildVisibility(true);
        when(visibilityService.findVisibility(CAMPAIGN_ID, OU_ID)).thenReturn(Optional.of(visibility));
        invokeGetNewCommunicationRequest(requestDto, surveyUnit, CURRENT_TIMESTAMP);
        verify(visibilityService).findVisibility(CAMPAIGN_ID, OU_ID);
    }

    @Test
    @DisplayName("When updating survey unit with persons provided, toModel() is used")
    void su11_testUpdateSurveyUnitInfosWithPersons() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = buildUpdateDtoWithAllData();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        when(dateService.getCurrentTimestamp()).thenReturn(CURRENT_TIMESTAMP);
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(surveyUnit.getPersons()).hasSize(1);
        assertThat(surveyUnit.getPersons()).first().satisfies(person -> {
            assertThat(person.getFirstName()).isEqualTo("John");
            assertThat(person.getLastName()).isEqualTo("Doe");
        });
    }

    @Test
    @DisplayName("When updating survey unit with identification provided as null, fallback to IdentificationDB.toModel()")
    void su12_testUpdateSurveyUnitInfosWithIdentificationFallback() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = buildUpdateDtoWithNullIdentification();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        when(dateService.getCurrentTimestamp()).thenReturn(CURRENT_TIMESTAMP);
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(surveyUnit.getIdentification()).isNotNull();
    }

    @Test
    @DisplayName("When updating survey unit with nextContactHistory provided, toModel() is used")
    void su13_testUpdateSurveyUnitInfosWithNextContactHistory() {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = buildUpdateDtoWithAllData();
        CommunicationTemplate template = buildCommunicationTemplate(CommunicationMedium.EMAIL);
        communicationTemplateRepository.save(template);
        when(dateService.getCurrentTimestamp()).thenReturn(CURRENT_TIMESTAMP);
        service.updateSurveyUnitInfos(surveyUnit, updateDto);
        assertThat(surveyUnit.getNextContactHistory()).isNotNull();
    }
}
