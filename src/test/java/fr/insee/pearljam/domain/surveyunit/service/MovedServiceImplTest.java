package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
    void updateMovedSurveyUnit_sets_priority_to_true_when_survey_unit_exists() {
        String surveyUnitId = "ABC-123";
        SurveyUnit surveyUnit = mock(SurveyUnit.class);
        when(surveyUnitRepository.findById(surveyUnitId)).thenReturn(Optional.of(surveyUnit));

        sut.updateMovedSurveyUnit(surveyUnitId);

        verify(surveyUnit, times(1)).setPriority(true);
        verify(surveyUnitRepository, times(1)).save(surveyUnit);
        verifyNoMoreInteractions(surveyUnit);
        verifyNoMoreInteractions(surveyUnitRepository);
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