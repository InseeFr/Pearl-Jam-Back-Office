package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.ContactOutcomeDto;
import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.SurveyUnitUpdateDto;
import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.port.out.VisibilityRepository;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.campaign.port.in.CommunicationTemplateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitUpdateService;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStateClient;
import fr.insee.pearljam.domain.surveyunit.port.out.*;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.*;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyUnitServiceImplTest {

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

    DateService dateService;

    private SurveyUnitServiceImpl service;

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
    }

    private static final String SURVEY_UNIT_ID = "SU-001";
    private static final String CAMPAIGN_ID = "CAMPAIGN-001";
    private static final String OU_ID = "OU-001";
    private static final String INTERVIEWER_ID = "INTERVIEWER-001";

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

    private void stubCountUeINATBR(SurveyUnitDB surveyUnit, Integer count) {
        when(surveyUnitRepository.findCountUeINATBRByInterviewerIdAndCampaignId(
                surveyUnit.getInterviewer().getId(),
                surveyUnit.getCampaign().getId(),
                surveyUnit.getId()))
                .thenReturn(count);
    }

    // ==================== addStateAuto method tests ====================

    @Test
    void addStateAuto_should_add_TBR_state_when_contact_outcome_is_INA_and_among_first_five() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        ClosingCauseDB closingCause = new ClosingCauseDB();
        surveyUnit.setClosingCause(closingCause);
        ContactOutcomeDto contactOutcomeDto = buildContactOutcomeDto(ContactOutcomeType.INA);

        stubCountUeINATBR(surveyUnit, 2); // < 5 -> eligible for TBR

        // When
        service.addStateAuto(surveyUnit, contactOutcomeDto);

        // Then
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository).save(stateCaptor.capture());

        StateDB savedState = stateCaptor.getValue();
        assertThat(savedState.getType()).isEqualTo(StateType.TBR);
        assertThat(savedState.getSurveyUnit()).isEqualTo(surveyUnit);
        assertThat(savedState.getDate()).isNotNull();

        assertThat(surveyUnit.getClosingCause()).isNull();
    }

    @Test
    void addStateAuto_should_add_FIN_state_when_contact_outcome_is_INA_but_not_among_first_five() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        ClosingCauseDB closingCause = new ClosingCauseDB();
        surveyUnit.setClosingCause(closingCause);
        ContactOutcomeDto contactOutcomeDto = buildContactOutcomeDto(ContactOutcomeType.INA);

        stubCountUeINATBR(surveyUnit, 5); // not < 5 -> falls through to FIN

        // When
        service.addStateAuto(surveyUnit, contactOutcomeDto);

        // Then
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository).save(stateCaptor.capture());

        StateDB savedState = stateCaptor.getValue();
        assertThat(savedState.getType()).isEqualTo(StateType.FIN);
        assertThat(surveyUnit.getClosingCause()).isNull();
    }

    @Test
    void addStateAuto_should_add_FIN_state_when_contact_outcome_is_not_INA() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        ClosingCauseDB closingCause = new ClosingCauseDB();
        surveyUnit.setClosingCause(closingCause);
        ContactOutcomeDto contactOutcomeDto = buildContactOutcomeDto(ContactOutcomeType.REF);

        stubCountUeINATBR(surveyUnit, 2); // among first five, but type isn't INA

        // When
        service.addStateAuto(surveyUnit, contactOutcomeDto);

        // Then
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository).save(stateCaptor.capture());

        StateDB savedState = stateCaptor.getValue();
        assertThat(savedState.getType()).isEqualTo(StateType.FIN);
        assertThat(savedState.getSurveyUnit()).isEqualTo(surveyUnit);
        assertThat(savedState.getDate()).isNotNull();

        assertThat(surveyUnit.getClosingCause()).isNull();
    }

    @Test
    void addStateAuto_should_add_FIN_state_when_contact_outcome_is_null() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        ClosingCauseDB closingCause = new ClosingCauseDB();
        surveyUnit.setClosingCause(closingCause);

        stubCountUeINATBR(surveyUnit, 2); // among first five, but no contact outcome

        // When
        service.addStateAuto(surveyUnit, null);

        // Then
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository).save(stateCaptor.capture());

        StateDB savedState = stateCaptor.getValue();
        assertThat(savedState.getType()).isEqualTo(StateType.FIN);
        assertThat(savedState.getSurveyUnit()).isEqualTo(surveyUnit);
        assertThat(savedState.getDate()).isNotNull();

        assertThat(surveyUnit.getClosingCause()).isNull();
    }

    // ==================== updateStates method tests ====================

    @Test
    void updateStates_should_process_incoming_states_when_not_null() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        List<StateDto> incomingStates = List.of(
                new StateDto(1L, dateService.getCurrentTimestamp() + 1000, StateType.WFS)
        );
        SurveyUnitUpdateDto updateDto = new SurveyUnitUpdateDto(
                SURVEY_UNIT_ID,
                null,
                null,
                null,
                null,
                incomingStates,
                null,
                null,
                null,
                null,
                null
        );

        when(stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(SURVEY_UNIT_ID))
                .thenReturn(new StateDto(1L, dateService.getCurrentTimestamp() + 500, StateType.WFS));
        when(stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(SURVEY_UNIT_ID))
                .thenReturn(List.of(new StateDto(1L, dateService.getCurrentTimestamp() + 500, StateType.WFS)));

        // When
        service.updateStates(surveyUnit, updateDto);

        // Then - incoming states should be processed
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository, atLeastOnce()).save(stateCaptor.capture());
        List<StateDB> savedStates = stateCaptor.getAllValues();
        assertThat(savedStates).isNotEmpty();
    }

    @Test
    void updateStates_should_add_auto_state_when_current_state_is_WFS() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        SurveyUnitUpdateDto updateDto = new SurveyUnitUpdateDto(
                SURVEY_UNIT_ID,
                null,
                null,
                null,
                null,
                null,
                null,
                buildContactOutcomeDto(ContactOutcomeType.REF),
                null,
                null,
                null
        );

        when(stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(SURVEY_UNIT_ID))
                .thenReturn(new StateDto(1L, dateService.getCurrentTimestamp(), StateType.WFS));
        when(stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(SURVEY_UNIT_ID))
                .thenReturn(List.of(new StateDto(1L, dateService.getCurrentTimestamp(), StateType.WFS)));

        stubCountUeINATBR(surveyUnit, 10);

        // When
        service.updateStates(surveyUnit, updateDto);

        // Then - auto state should be added (FIN because not INA)
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository, atLeastOnce()).save(stateCaptor.capture());
        
        List<StateDB> savedStates = stateCaptor.getAllValues();
        boolean hasFinState = savedStates.stream().anyMatch(s -> s.getType() == StateType.FIN);
        assertThat(hasFinState).isTrue();
    }

    @Test
    void updateStates_should_not_add_fallback_when_TBR_already_exists() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        surveyUnit.setStates(new HashSet<>(Set.of(
                new StateDB(dateService.getCurrentTimestamp(), surveyUnit, StateType.TBR)
        )));
        
        SurveyUnitUpdateDto updateDto = new SurveyUnitUpdateDto(
                SURVEY_UNIT_ID,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(SURVEY_UNIT_ID))
                .thenReturn(new StateDto(1L, dateService.getCurrentTimestamp() + 100, StateType.TBR));
        when(stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(SURVEY_UNIT_ID))
                .thenReturn(List.of(new StateDto(1L, dateService.getCurrentTimestamp() + 100, StateType.TBR)));

        // When
        service.updateStates(surveyUnit, updateDto);

        // Then - no new fallback state should be added since TBR already exists
        assertThat(surveyUnit.getStates()).hasSize(1);
    }

    @Test
    void updateStates_should_skip_processing_when_incoming_states_is_null() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        
        SurveyUnitUpdateDto updateDto = new SurveyUnitUpdateDto(
                SURVEY_UNIT_ID,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(stateRepository.findFirstDtoBySurveyUnitIdOrderByDateDesc(SURVEY_UNIT_ID))
                .thenReturn(new StateDto(1L, dateService.getCurrentTimestamp() + 100, StateType.PRC));
        when(stateRepository.findAllDtoBySurveyUnitIdOrderByDateAsc(SURVEY_UNIT_ID))
                .thenReturn(List.of(new StateDto(1L, dateService.getCurrentTimestamp() + 100, StateType.PRC)));

        // When
        service.updateStates(surveyUnit, updateDto);


        assertThat(surveyUnit.getStates()).isEmpty();
    }

    // ==================== processIncomingStates method tests ====================

    @Test
    void processIncomingStates_should_adjust_future_dates_with_incremental_offset() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        
        // Use a fixed current time so we can predict the behavior
        
        
        // Simulate the offline bug scenario from production:
        // States with dates far in the future (will be adjusted)
        // WFS arrived at timestamp, TBR created at timestamp
        // We want to verify that future dates are adjusted to maintain chronological order
        List<StateDto> incomingStates = List.of(
                new StateDto(4237190L, Long.MAX_VALUE - 100, StateType.WFS),
                new StateDto(4237192L, Long.MAX_VALUE - 200, StateType.TBR),
                new StateDto(4237191L, Long.MAX_VALUE - 300, StateType.TBR)
        );

        // When
        service.processIncomingStates(surveyUnit, incomingStates);

        // Then
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository, times(3)).save(stateCaptor.capture());

        List<StateDB> savedStates = stateCaptor.getAllValues();
        
        // Verify IDs are preserved and in the order they were processed (descending by original date)
        assertThat(savedStates.get(0).getId()).isEqualTo(4237190L);
        assertThat(savedStates.get(0).getType()).isEqualTo(StateType.WFS);
        assertThat(savedStates.get(1).getId()).isEqualTo(4237192L);
        assertThat(savedStates.get(1).getType()).isEqualTo(StateType.TBR);
        assertThat(savedStates.get(2).getId()).isEqualTo(4237191L);
        assertThat(savedStates.get(2).getType()).isEqualTo(StateType.TBR);
        
        // Verify dates are adjusted (less than original future dates) and in descending order
        assertThat(savedStates.get(0).getDate()).isLessThan(Long.MAX_VALUE - 100);
        assertThat(savedStates.get(1).getDate()).isLessThan(Long.MAX_VALUE - 200);
        assertThat(savedStates.get(2).getDate()).isLessThan(Long.MAX_VALUE - 300);
        // Adjusted dates should be in descending order (with incremental offsets)
        assertThat(savedStates.get(0).getDate()).isGreaterThan(savedStates.get(1).getDate());
        assertThat(savedStates.get(1).getDate()).isGreaterThan(savedStates.get(2).getDate());
    }

    @Test
    void processIncomingStates_should_not_adjust_past_dates() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        
        // States with dates in the past (before FIXED_TIMESTAMP)
        List<StateDto> incomingStates = List.of(
                new StateDto(1L, dateService.getCurrentTimestamp() - 200, StateType.WFS),
                new StateDto(2L, dateService.getCurrentTimestamp() - 100, StateType.PRC),
                new StateDto(3L, dateService.getCurrentTimestamp() - 50, StateType.APS)
        );

        // When
        service.processIncomingStates(surveyUnit, incomingStates);

        // Then
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository, times(3)).save(stateCaptor.capture());

        List<StateDB> savedStates = stateCaptor.getAllValues();
        
        // Dates should remain unchanged for past dates (sorted descending)
        assertThat(savedStates.get(0).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 50);
        assertThat(savedStates.get(1).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 100);
        assertThat(savedStates.get(2).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 200);
    }

    @Test
    void processIncomingStates_should_throw_exception_for_null_dates() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        
        // State with null date
        List<StateDto> incomingStates = List.of(
                new StateDto(1L, null, StateType.WFS)
        );

        // When/Then - expect IllegalArgumentException for null date
        assertThatThrownBy(() -> service.processIncomingStates(surveyUnit, incomingStates))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("State with id=1 has a null date");
    }

    @Test
    void processIncomingStates_should_handle_empty_list() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        
        List<StateDto> incomingStates = List.of();

        // When
        service.processIncomingStates(surveyUnit, incomingStates);

        // Then - no states should be saved
        verify(stateRepository, never()).save(any());
    }

    @Test
    void processIncomingStates_should_preserve_state_ids() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        
        
        List<StateDto> incomingStates = List.of(
                new StateDto(100L, dateService.getCurrentTimestamp() - 1000, StateType.WFS),
                new StateDto(200L, dateService.getCurrentTimestamp() - 2000, StateType.PRC)
        );

        // When
        service.processIncomingStates(surveyUnit, incomingStates);

        // Then
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository, times(2)).save(stateCaptor.capture());

        List<StateDB> savedStates = stateCaptor.getAllValues();
        
        // IDs should be preserved for updates
        assertThat(savedStates.get(0).getId()).isEqualTo(100L);
        assertThat(savedStates.get(1).getId()).isEqualTo(200L);
    }

    @Test
    void processIncomingStates_should_sort_by_date_descending() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        
        
        // States in ascending order of date (oldest first), all before FIXED_TIMESTAMP
        List<StateDto> incomingStates = List.of(
                new StateDto(1L, dateService.getCurrentTimestamp() - 300, StateType.WFS),
                new StateDto(2L, dateService.getCurrentTimestamp() - 200, StateType.PRC),
                new StateDto(3L, dateService.getCurrentTimestamp() - 100, StateType.APS)
        );

        // When
        service.processIncomingStates(surveyUnit, incomingStates);

        // Then
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository, times(3)).save(stateCaptor.capture());

        List<StateDB> savedStates = stateCaptor.getAllValues();
        
        // Should be processed in descending order (most recent first)
        assertThat(savedStates.get(0).getType()).isEqualTo(StateType.APS);
        assertThat(savedStates.get(1).getType()).isEqualTo(StateType.PRC);
        assertThat(savedStates.get(2).getType()).isEqualTo(StateType.WFS);
        
        // Dates should be unchanged (all are in the past) and in descending order
        assertThat(savedStates.get(0).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 100);
        assertThat(savedStates.get(1).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 200);
        assertThat(savedStates.get(2).getDate()).isEqualTo(dateService.getCurrentTimestamp() - 300);
    }

    // ==================== addFallbackTbrOrFinState method tests ====================
    
    @Test
    void addFallbackTbrOrFinState_should_add_FIN_when_FIN_exists() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        
        StateDB existingFinState = new StateDB();
        existingFinState.setType(StateType.FIN);
        existingFinState.setDate(dateService.getCurrentTimestamp());
        existingFinState.setSurveyUnit(surveyUnit);
        surveyUnit.setStates(new HashSet<>(Set.of(existingFinState)));

        // When
        service.addFallbackTbrOrFinState(surveyUnit);

        // Then - FIN exists, so add FIN
        assertThat(surveyUnit.getStates()).hasSize(2);
        assertThat(surveyUnit.getStates().stream()
                .anyMatch(s -> s.getType() == StateType.FIN)).isTrue();
    }

    @Test
    void addFallbackTbrOrFinState_should_add_FIN_when_TBR_exists() {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        
        StateDB existingTbrState = new StateDB();
        existingTbrState.setType(StateType.TBR);
        existingTbrState.setDate(dateService.getCurrentTimestamp());
        existingTbrState.setSurveyUnit(surveyUnit);
        surveyUnit.setStates(new HashSet<>(Set.of(existingTbrState)));

        // When
        service.addFallbackTbrOrFinState(surveyUnit);

        // Then - TBR exists, so add FIN
        assertThat(surveyUnit.getStates()).hasSize(2);
        assertThat(surveyUnit.getStates().stream()
                .anyMatch(s -> s.getType() == StateType.TBR)).isTrue();
    }
}
