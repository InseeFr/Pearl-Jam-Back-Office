package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.domain.OtherModeQuestionnaireState;
import fr.insee.pearljam.api.repository.OtherModeQuestionnaireRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.model.OtherModeQuestionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import fr.insee.pearljam.api.domain.SurveyUnit; // ← ajustez si nécessaire

@ExtendWith(MockitoExtension.class)
class OtherModeQuestionnaireServiceImplTest {

    @Mock
    private OtherModeQuestionnaireRepository otherModeQuestionnaireRepository;

    @Mock
    private SurveyUnitRepository surveyUnitRepository;

    @Mock
    private OtherModeQuestionnaire input; // objet domaine d'entrée

    private OtherModeQuestionnaireServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OtherModeQuestionnaireServiceImpl(otherModeQuestionnaireRepository, surveyUnitRepository);
    }

    @Test
    void addOtherModeQuestionnaire_should_save_when_surveyUnit_exists() {
        String surveyUnitId = "SU-123";
        String type = "QUESTIONNAIRE_INIT";

        when(input.surveyUnitId()).thenReturn(surveyUnitId);
        when(input.type()).thenReturn(type);

        SurveyUnit su = mock(SurveyUnit.class);
        when(surveyUnitRepository.findById(surveyUnitId)).thenReturn(Optional.of(su));

        ArgumentCaptor<OtherModeQuestionnaireState> stateCaptor =
                ArgumentCaptor.forClass(OtherModeQuestionnaireState.class);

        service.addOtherModeQuestionnaire(input);

        verify(surveyUnitRepository, times(1)).findById(surveyUnitId);
        verify(otherModeQuestionnaireRepository, times(1)).save(stateCaptor.capture());
        verifyNoMoreInteractions(otherModeQuestionnaireRepository);

        OtherModeQuestionnaireState saved = stateCaptor.getValue();
        assertThat(saved).isNotNull();

        assertThat(saved.getState()).isEqualTo(type);

        assertThat(saved.getSurveyUnit()).isSameAs(su);
    }

    @Test
    void addOtherModeQuestionnaire_should_not_save_when_surveyUnit_absent() {
        String surveyUnitId = "SU-404";
        when(input.surveyUnitId()).thenReturn(surveyUnitId);
        when(input.type()).thenReturn("ANY");

        when(surveyUnitRepository.findById(surveyUnitId)).thenReturn(Optional.empty());

        service.addOtherModeQuestionnaire(input);

        verify(surveyUnitRepository, times(1)).findById(surveyUnitId);
        verify(otherModeQuestionnaireRepository, never()).save(any());
        verifyNoMoreInteractions(otherModeQuestionnaireRepository);
    }

    @Test
    void addOtherModeQuestionnaire_should_call_repositories_with_correct_arguments() {
        String surveyUnitId = "SU-789";
        when(input.surveyUnitId()).thenReturn(surveyUnitId);
        when(input.type()).thenReturn("WEB");

        when(surveyUnitRepository.findById(surveyUnitId)).thenReturn(Optional.of(mock(SurveyUnit.class)));

        service.addOtherModeQuestionnaire(input);

        verify(surveyUnitRepository).findById(eq(surveyUnitId));
        verify(otherModeQuestionnaireRepository).save(any(OtherModeQuestionnaireState.class));
    }
}