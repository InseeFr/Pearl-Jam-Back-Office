package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.infrastructure.surveyunit.entity.identification.IdentificationDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovedServiceImplTest {

    @Mock
    private SurveyUnitRepository surveyUnitRepository;

    private MovedServiceImpl sut;

    @BeforeEach
    void setUp() {
        sut = new MovedServiceImpl(surveyUnitRepository);
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateMovedSurveyUnit_sets_priority_and_demenagement_web_when_survey_unit_exists() {
        String surveyUnitId = "ABC-123";
        SurveyUnit surveyUnit = mock(SurveyUnit.class);
        IdentificationDB identification = mock(IdentificationDB.class);
        Set persons = new HashSet<>();
        persons.add(new Object()); // Add an element to verify it gets cleared

        when(surveyUnitRepository.findById(surveyUnitId)).thenReturn(Optional.of(surveyUnit));
        when(surveyUnit.getIdentification()).thenReturn(identification);
        when(surveyUnit.getPersons()).thenReturn(persons);

        sut.updateMovedSurveyUnit(surveyUnitId);

        // Verify priority is set
        verify(surveyUnit, times(1)).setPriority(true);

        // Verify state is set to INS
        ArgumentCaptor<Set<State>> statesCaptor = ArgumentCaptor.forClass(Set.class);
        verify(surveyUnit, times(1)).setStates(statesCaptor.capture());
        Set<State> capturedStates = statesCaptor.getValue();
        assertThat(capturedStates).hasSize(1);
        State capturedState = capturedStates.iterator().next();
        assertThat(capturedState.getType()).isEqualTo(StateType.INS);

        verify(surveyUnit, times(1)).getIdentification();
        verify(identification, times(1)).setDemenagementWeb(true);
        verify(surveyUnit, times(1)).getPersons();
        assertThat(persons).isEmpty(); // Verify persons were cleared
        verify(surveyUnit, never()).setIdentification(null);
        verify(surveyUnitRepository, times(1)).save(surveyUnit);
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateMovedSurveyUnit_handles_null_identification_gracefully() {
        String surveyUnitId = "ABC-123";
        SurveyUnit surveyUnit = mock(SurveyUnit.class);
        Set persons = new HashSet<>();
        persons.add(new Object()); // Add an element to verify it gets cleared

        when(surveyUnitRepository.findById(surveyUnitId)).thenReturn(Optional.of(surveyUnit));
        when(surveyUnit.getIdentification()).thenReturn(null);
        when(surveyUnit.getPersons()).thenReturn(persons);

        sut.updateMovedSurveyUnit(surveyUnitId);

        // Verify priority is set
        verify(surveyUnit, times(1)).setPriority(true);

        // Verify state is set to INS
        ArgumentCaptor<Set<State>> statesCaptor = ArgumentCaptor.forClass(Set.class);
        verify(surveyUnit, times(1)).setStates(statesCaptor.capture());
        Set<State> capturedStates = statesCaptor.getValue();
        assertThat(capturedStates).hasSize(1);
        State capturedState = capturedStates.iterator().next();
        assertThat(capturedState.getType()).isEqualTo(StateType.INS);

        verify(surveyUnit, times(1)).getIdentification();
        verify(surveyUnit, times(1)).getPersons();
        assertThat(persons).isEmpty(); // Verify persons were cleared even with null identification
        verify(surveyUnitRepository, times(1)).save(surveyUnit);
    }

    @Test
    void updateMovedSurveyUnit_does_nothing_when_survey_unit_not_found() {
        String surveyUnitId = "NON-EXISTING-ID";
        when(surveyUnitRepository.findById(surveyUnitId)).thenReturn(Optional.empty());

        sut.updateMovedSurveyUnit(surveyUnitId);

        verify(surveyUnitRepository, times(1)).findById(surveyUnitId);
        verify(surveyUnitRepository, never()).save(any());
        verifyNoMoreInteractions(surveyUnitRepository);
    }
}