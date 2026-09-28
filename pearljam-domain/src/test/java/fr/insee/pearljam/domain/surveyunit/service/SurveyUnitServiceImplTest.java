package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.ContactOutcomeDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.SurveyUnitUpdateDto;
import fr.insee.pearljam.domain.campaign.port.in.CommunicationTemplateService;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.port.out.VisibilityRepository;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitUpdateService;
import fr.insee.pearljam.domain.surveyunit.port.out.*;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Private methods (updateStates, processIncomingStates, addStateAuto,
 * addFallbackTbrOrFinState) are exercised only through the public
 * {@link SurveyUnitServiceImpl#updateSurveyUnit(String, String, SurveyUnitUpdateDto)}.
 */
@ExtendWith(MockitoExtension.class)
class SurveyUnitServiceImplTest {

    private static final String SURVEY_UNIT_ID = "SU-001";
    private static final String CAMPAIGN_ID = "CAMPAIGN-001";
    private static final String OU_ID = "OU-001";
    private static final String INTERVIEWER_ID = "INTERVIEWER-001";

    @Mock
    private SurveyUnitRepository surveyUnitRepository;
    @Mock
    private SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private StateRepository stateRepository;
    @Mock
    private InterviewerRepository interviewerRepository;
    @Mock
    private CampaignRepository campaignRepository;
    @Mock
    private OrganizationUnitRepository organizationUnitRepository;
    @Mock
    private VisibilityRepository visibilityRepository;
    @Mock
    private ClosingCauseRepository closingCauseRepository;
    @Mock
    private UserService userService;
    @Mock
    private QuestionnaireStateClient questionnaireStateClient;
    @Mock
    private SurveyUnitUpdateService surveyUnitUpdateService;
    @Mock
    private CommunicationTemplateService communicationTemplateService;
    @Mock
    private JsonMapper jsonMapper;

    private DateService dateService;
    private SurveyUnitServiceImpl service;

    /** Real business rules, except the fallback decision which each test controls (default: no fallback). */
    private MockedStatic<StateBusinessRules> businessRules;

    @BeforeEach
    void setUp() {
        dateService = new FixedDateService();
        service = new SurveyUnitServiceImpl(
                surveyUnitRepository,
                surveyUnitTempZoneRepository,
                addressRepository,
                stateRepository,
                interviewerRepository,
                campaignRepository,
                organizationUnitRepository,
                visibilityRepository,
                closingCauseRepository,
                userService,
                questionnaireStateClient,
                surveyUnitUpdateService,
                communicationTemplateService,
                dateService,
                jsonMapper
        );

        businessRules = mockStatic(StateBusinessRules.class, CALLS_REAL_METHODS);
        stubFallbackDecision(false);
    }

    @AfterEach
    void tearDown() {
        businessRules.close();
    }

    // ==================== helpers ====================

    private SurveyUnitDB buildTestSurveyUnit() {
        CampaignDB campaign = new CampaignDB();
        campaign.setId(CAMPAIGN_ID);

        OrganizationUnitDB ou = new OrganizationUnitDB();
        ou.setId(OU_ID);

        InterviewerDB interviewer = new InterviewerDB();
        interviewer.setId(INTERVIEWER_ID);

        SurveyUnitDB surveyUnit = new SurveyUnitDB();
        surveyUnit.setId(SURVEY_UNIT_ID);
        surveyUnit.setCampaign(campaign);
        surveyUnit.setOrganizationUnit(ou);
        surveyUnit.setInterviewer(interviewer);
        surveyUnit.setStates(new HashSet<>());
        return surveyUnit;
    }

    private ContactOutcomeDto buildContactOutcomeDto(ContactOutcomeType type) {
        return new ContactOutcomeDto(dateService.getCurrentTimestamp() + 1000, type, 1);
    }

    private SurveyUnitUpdateDto buildUpdateDto(List<StateDto> states, ContactOutcomeDto contactOutcome) {
        return new SurveyUnitUpdateDto(
                SURVEY_UNIT_ID,
                null,
                null,
                null,
                null,
                states,
                null,
                contactOutcome,
                null,
                null,
                null
        );
    }

    /** Stubs the lookups done at the beginning and at the end of updateSurveyUnit. */
    private void stubSurveyUnitLookup(SurveyUnitDB surveyUnit) {
        when(surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(SURVEY_UNIT_ID, INTERVIEWER_ID))
                .thenReturn(Optional.of(surveyUnit));
        when(surveyUnitRepository.findById(SURVEY_UNIT_ID))
                .thenReturn(Optional.of(surveyUnit));
    }

    private void stubCurrentState(StateType type) {
        when(stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(SURVEY_UNIT_ID))
                .thenReturn(new StateDto(1L, dateService.getCurrentTimestamp(), type));
    }

    private void stubCountUeINATBR(SurveyUnitDB surveyUnit, Integer count) {
        when(surveyUnitRepository.findCountUeINATBRByInterviewerIdAndCampaignId(
                surveyUnit.getInterviewer().getId(),
                surveyUnit.getCampaign().getId(),
                surveyUnit.getId()))
                .thenReturn(count);
    }

    private void stubFallbackDecision(boolean shouldFallBack) {
        businessRules
                .when(() -> StateBusinessRules.shouldFallBackToTbrOrFin(any()))
                .thenReturn(shouldFallBack);
    }

    private List<StateDB> captureSavedStates(int expectedTimes) {
        ArgumentCaptor<StateDB> captor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository, times(expectedTimes)).save(captor.capture());
        return captor.getAllValues();
    }

    // ==================== addStateAuto (via updateSurveyUnit) ====================

    @Test
    void updateSurveyUnit_should_add_TBR_state_when_current_is_WFS_and_outcome_is_INA_and_among_first_five() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        surveyUnit.setClosingCause(new ClosingCauseDB());
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.WFS);
        stubCountUeINATBR(surveyUnit, 2); // < 5 -> eligible for TBR

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID,
                buildUpdateDto(null, buildContactOutcomeDto(ContactOutcomeType.INA)));

        // Then
        StateDB savedState = captureSavedStates(1).getFirst();
        assertThat(savedState.getType()).isEqualTo(StateType.TBR);
        assertThat(savedState.getSurveyUnit()).isEqualTo(surveyUnit);
        assertThat(savedState.getDate()).isNotNull();
        assertThat(surveyUnit.getClosingCause()).isNull();
    }

    @Test
    void updateSurveyUnit_should_add_FIN_state_when_outcome_is_INA_but_not_among_first_five() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        surveyUnit.setClosingCause(new ClosingCauseDB());
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.WFS);
        stubCountUeINATBR(surveyUnit, 5); // not < 5 -> FIN

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID,
                buildUpdateDto(null, buildContactOutcomeDto(ContactOutcomeType.INA)));

        // Then
        StateDB savedState = captureSavedStates(1).getFirst();
        assertThat(savedState.getType()).isEqualTo(StateType.FIN);
        assertThat(surveyUnit.getClosingCause()).isNull();
    }

    @Test
    void updateSurveyUnit_should_add_FIN_state_when_outcome_is_not_INA() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        surveyUnit.setClosingCause(new ClosingCauseDB());
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.WFS);
        stubCountUeINATBR(surveyUnit, 2); // among first five, but outcome isn't INA

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID,
                buildUpdateDto(null, buildContactOutcomeDto(ContactOutcomeType.REF)));

        // Then
        StateDB savedState = captureSavedStates(1).getFirst();
        assertThat(savedState.getType()).isEqualTo(StateType.FIN);
        assertThat(savedState.getSurveyUnit()).isEqualTo(surveyUnit);
        assertThat(savedState.getDate()).isNotNull();
        assertThat(surveyUnit.getClosingCause()).isNull();
    }

    @Test
    void updateSurveyUnit_should_add_FIN_state_when_contact_outcome_is_null() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        surveyUnit.setClosingCause(new ClosingCauseDB());
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.WFS);
        stubCountUeINATBR(surveyUnit, 2); // among first five, but no contact outcome

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(null, null));

        // Then
        StateDB savedState = captureSavedStates(1).getFirst();
        assertThat(savedState.getType()).isEqualTo(StateType.FIN);
        assertThat(savedState.getSurveyUnit()).isEqualTo(surveyUnit);
        assertThat(savedState.getDate()).isNotNull();
        assertThat(surveyUnit.getClosingCause()).isNull();
    }

    @Test
    void updateSurveyUnit_should_not_add_auto_state_when_current_state_is_not_WFS() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.PRC);

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID,
                buildUpdateDto(null, buildContactOutcomeDto(ContactOutcomeType.REF)));

        // Then
        verify(stateRepository, never()).save(any());
        verify(surveyUnitRepository, never())
                .findCountUeINATBRByInterviewerIdAndCampaignId(any(), any(), any());
    }

    // ==================== updateStates (via updateSurveyUnit) ====================

    @Test
    void updateSurveyUnit_should_process_incoming_states_when_not_null() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.PRC);
        List<StateDto> incomingStates = List.of(
                new StateDto(1L, dateService.getCurrentTimestamp() - 1000, StateType.WFS)
        );

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(incomingStates, null));

        // Then
        List<StateDB> savedStates = captureSavedStates(1);
        assertThat(savedStates.getFirst().getType()).isEqualTo(StateType.WFS);
    }

    @Test
    void updateSurveyUnit_should_skip_incoming_states_processing_when_states_is_null() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.PRC);

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(null, null));

        // Then
        verify(stateRepository, never()).save(any());
        verify(stateRepository, never()).findAllByIds(any());
        assertThat(surveyUnit.getStates()).isEmpty();
    }

    @Test
    void updateSurveyUnit_should_not_add_fallback_state_when_rules_do_not_require_it() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        surveyUnit.setStates(new HashSet<>(Set.of(
                new StateDB(dateService.getCurrentTimestamp(), surveyUnit, StateType.TBR)
        )));
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.TBR);
        stubFallbackDecision(false);

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(null, null));

        // Then
        assertThat(surveyUnit.getStates()).hasSize(1);
    }

    // ==================== processIncomingStates (via updateSurveyUnit) ====================

    @Test
    void updateSurveyUnit_should_adjust_future_dates_with_incremental_offset() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.PRC);

        // Offline bug scenario: states dated far in the future must be pulled back
        // while keeping their chronological order
        List<StateDto> incomingStates = List.of(
                new StateDto(4237190L, Long.MAX_VALUE - 100, StateType.WFS),
                new StateDto(4237192L, Long.MAX_VALUE - 200, StateType.TBR),
                new StateDto(4237191L, Long.MAX_VALUE - 300, StateType.TBR)
        );

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(incomingStates, null));

        // Then
        List<StateDB> savedStates = captureSavedStates(3);

        // IDs preserved, processed in descending order of original date
        assertThat(savedStates.get(0).getId()).isEqualTo(4237190L);
        assertThat(savedStates.get(0).getType()).isEqualTo(StateType.WFS);
        assertThat(savedStates.get(1).getId()).isEqualTo(4237192L);
        assertThat(savedStates.get(1).getType()).isEqualTo(StateType.TBR);
        assertThat(savedStates.get(2).getId()).isEqualTo(4237191L);
        assertThat(savedStates.get(2).getType()).isEqualTo(StateType.TBR);

        // Dates adjusted (strictly lower than the future ones)
        assertThat(savedStates.get(0).getDate()).isLessThan(Long.MAX_VALUE - 100);
        assertThat(savedStates.get(1).getDate()).isLessThan(Long.MAX_VALUE - 200);
        assertThat(savedStates.get(2).getDate()).isLessThan(Long.MAX_VALUE - 300);

        // ...and still in descending order thanks to the incremental offset
        assertThat(savedStates.get(0).getDate()).isGreaterThan(savedStates.get(1).getDate());
        assertThat(savedStates.get(1).getDate()).isGreaterThan(savedStates.get(2).getDate());
    }

    @Test
    void updateSurveyUnit_should_not_adjust_past_dates() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.PRC);
        List<StateDto> incomingStates = List.of(
                new StateDto(1L, dateService.getCurrentTimestamp() - 200, StateType.WFS),
                new StateDto(2L, dateService.getCurrentTimestamp() - 100, StateType.PRC),
                new StateDto(3L, dateService.getCurrentTimestamp() - 50, StateType.APS)
        );

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(incomingStates, null));

        // Then - dates unchanged (sorted descending)
        List<StateDB> savedStates = captureSavedStates(3);
        assertThat(savedStates.get(0).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 50);
        assertThat(savedStates.get(1).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 100);
        assertThat(savedStates.get(2).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 200);
    }

    @Test
    void updateSurveyUnit_should_throw_exception_for_incoming_state_with_null_date() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        when(surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(SURVEY_UNIT_ID, INTERVIEWER_ID))
                .thenReturn(Optional.of(surveyUnit));
        List<StateDto> incomingStates = List.of(new StateDto(1L, null, StateType.WFS));
        SurveyUnitUpdateDto updateDto = buildUpdateDto(incomingStates, null);

        // When / Then
        assertThatThrownBy(() -> service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("State with id=1 has a null date");
        verify(stateRepository, never()).save(any());
    }

    @Test
    void updateSurveyUnit_should_not_save_any_state_when_incoming_states_is_empty() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.PRC);

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(List.of(), null));

        // Then
        verify(stateRepository, never()).save(any());
    }

    @Test
    void updateSurveyUnit_should_preserve_state_ids() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.PRC);
        List<StateDto> incomingStates = List.of(
                new StateDto(100L, dateService.getCurrentTimestamp() - 1000, StateType.WFS),
                new StateDto(200L, dateService.getCurrentTimestamp() - 2000, StateType.PRC)
        );

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(incomingStates, null));

        // Then
        List<StateDB> savedStates = captureSavedStates(2);
        assertThat(savedStates.get(0).getId()).isEqualTo(100L);
        assertThat(savedStates.get(1).getId()).isEqualTo(200L);
    }

    @Test
    void updateSurveyUnit_should_sort_incoming_states_by_date_descending() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.PRC);
        // Ascending order of date (oldest first), all in the past
        List<StateDto> incomingStates = List.of(
                new StateDto(1L, dateService.getCurrentTimestamp() - 300, StateType.WFS),
                new StateDto(2L, dateService.getCurrentTimestamp() - 200, StateType.PRC),
                new StateDto(3L, dateService.getCurrentTimestamp() - 100, StateType.APS)
        );

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(incomingStates, null));

        // Then - most recent first, dates unchanged
        List<StateDB> savedStates = captureSavedStates(3);
        assertThat(savedStates.get(0).getType()).isEqualTo(StateType.APS);
        assertThat(savedStates.get(1).getType()).isEqualTo(StateType.PRC);
        assertThat(savedStates.get(2).getType()).isEqualTo(StateType.WFS);
        assertThat(savedStates.get(0).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 100);
        assertThat(savedStates.get(1).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 200);
        assertThat(savedStates.get(2).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 300);
    }

    @Test
    void updateSurveyUnit_should_not_save_incoming_states_that_already_exist() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.PRC);

        StateDB existing = new StateDB(dateService.getCurrentTimestamp() - 500, surveyUnit, StateType.WFS);
        existing.setId(100L);
        when(stateRepository.findAllByIds(any())).thenReturn(List.of(existing));

        List<StateDto> incomingStates = List.of(
                new StateDto(100L, dateService.getCurrentTimestamp() - 500, StateType.WFS),
                new StateDto(200L, dateService.getCurrentTimestamp() - 100, StateType.PRC)
        );

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(incomingStates, null));

        // Then - only the unknown state is saved
        List<StateDB> savedStates = captureSavedStates(1);
        assertThat(savedStates.getFirst().getId()).isEqualTo(200L);
    }

    // ==================== addFallbackTbrOrFinState (via updateSurveyUnit) ====================

    @Test
    void updateSurveyUnit_should_add_FIN_fallback_when_FIN_exists() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        StateDB existingFin = new StateDB();
        existingFin.setType(StateType.FIN);
        existingFin.setDate(dateService.getCurrentTimestamp());
        existingFin.setSurveyUnit(surveyUnit);
        surveyUnit.setStates(new HashSet<>(Set.of(existingFin)));

        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.FIN);
        stubFallbackDecision(true);

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(null, null));

        // Then
        assertThat(surveyUnit.getStates()).hasSize(2);
        assertThat(surveyUnit.getStates()).allMatch(s -> s.getType() == StateType.FIN);
    }

    @Test
    void updateSurveyUnit_should_add_TBR_fallback_when_only_TBR_exists() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        StateDB existingTbr = new StateDB();
        existingTbr.setType(StateType.TBR);
        existingTbr.setDate(dateService.getCurrentTimestamp());
        existingTbr.setSurveyUnit(surveyUnit);
        surveyUnit.setStates(new HashSet<>(Set.of(existingTbr)));

        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.TBR);
        stubFallbackDecision(true);

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(null, null));

        // Then
        assertThat(surveyUnit.getStates()).hasSize(2);
        assertThat(surveyUnit.getStates()).allMatch(s -> s.getType() == StateType.TBR);
    }

    @Test
    void updateSurveyUnit_should_prefer_FIN_fallback_when_both_FIN_and_TBR_exist() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        StateDB existingTbr = new StateDB(dateService.getCurrentTimestamp() - 10, surveyUnit, StateType.TBR);
        StateDB existingFin = new StateDB(dateService.getCurrentTimestamp() - 5, surveyUnit, StateType.FIN);
        surveyUnit.setStates(new HashSet<>(Set.of(existingTbr, existingFin)));

        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.FIN);
        stubFallbackDecision(true);

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(null, null));

        // Then
        assertThat(surveyUnit.getStates()).hasSize(3);
        assertThat(surveyUnit.getStates().stream().filter(s -> s.getType() == StateType.FIN)).hasSize(2);
        assertThat(surveyUnit.getStates().stream().filter(s -> s.getType() == StateType.TBR)).hasSize(1);
    }

    @Test
    void updateSurveyUnit_should_not_add_fallback_when_neither_FIN_nor_TBR_exists() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        stubSurveyUnitLookup(surveyUnit);
        stubCurrentState(StateType.PRC);
        stubFallbackDecision(true);

        // When
        service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, buildUpdateDto(null, null));

        // Then
        assertThat(surveyUnit.getStates()).isEmpty();
    }

    // ==================== updateSurveyUnit guard ====================

    @Test
    void updateSurveyUnit_should_throw_when_survey_unit_not_found_for_interviewer() {
        // Given
        when(surveyUnitRepository.findByIdAndInterviewerIdIgnoreCase(SURVEY_UNIT_ID, INTERVIEWER_ID))
                .thenReturn(Optional.empty());
        SurveyUnitUpdateDto updateDto = buildUpdateDto(null, null);

        // When / Then
        assertThatThrownBy(() -> service.updateSurveyUnit(INTERVIEWER_ID, SURVEY_UNIT_ID, updateDto))
                .isInstanceOf(SurveyUnitNotFoundException.class);
        verifyNoInteractions(stateRepository);
    }
}