package fr.insee.pearljam.infrastructure.crossenvironmentcommunication.db.adapter;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.crossenvironmentcommunication.model.SurveyUnitStatus;
import fr.insee.pearljam.infrastructure.crossenvironmentcommunication.db.entity.SurveyUnitStatusEntity;
import fr.insee.pearljam.infrastructure.crossenvironmentcommunication.db.jpa.SurveyUnitStatusJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyUnitStatusRepositoryAdapterTest {

    @Mock
    private SurveyUnitStatusJpaRepository crudRepository;

    @Mock
    private SurveyUnitRepository surveyUnitRepository;

    @Mock
    private SurveyUnit surveyUnit;

    private SurveyUnitStatusRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SurveyUnitStatusRepositoryAdapter(crudRepository, surveyUnitRepository);
    }

    @Test
    @DisplayName("Should save status when survey unit exists")
    void testSaveStatusWhenSurveyUnitExists() {
        // Given
        String surveyUnitId = "survey-unit-123";
        String state = "QUESTIONNAIRE_INIT";
        SurveyUnitStatus status = new SurveyUnitStatus(surveyUnitId, state);

        when(surveyUnitRepository.findById(surveyUnitId)).thenReturn(Optional.of(surveyUnit));

        // When
        adapter.save(status);

        // Then
        ArgumentCaptor<SurveyUnitStatusEntity> entityCaptor = ArgumentCaptor.forClass(SurveyUnitStatusEntity.class);
        verify(crudRepository, times(1)).save(entityCaptor.capture());

        SurveyUnitStatusEntity capturedEntity = entityCaptor.getValue();
        assertThat(capturedEntity.getState()).isEqualTo(state);
        assertThat(capturedEntity.getSurveyUnit()).isEqualTo(surveyUnit);
    }

    @Test
    @DisplayName("Should not save status when survey unit does not exist")
    void testSaveStatusWhenSurveyUnitDoesNotExist() {
        // Given
        String surveyUnitId = "non-existent-survey-unit";
        String state = "QUESTIONNAIRE_COMPLETED";
        SurveyUnitStatus status = new SurveyUnitStatus(surveyUnitId, state);

        when(surveyUnitRepository.findById(surveyUnitId)).thenReturn(Optional.empty());

        // When
        adapter.save(status);

        // Then
        verify(crudRepository, never()).save(any(SurveyUnitStatusEntity.class));
    }

    @Test
    @DisplayName("Should call surveyUnitRepository findById with correct ID")
    void testSaveCallsFindByIdWithCorrectId() {
        // Given
        String surveyUnitId = "survey-unit-456";
        String state = "QUESTIONNAIRE_VALIDATED";
        SurveyUnitStatus status = new SurveyUnitStatus(surveyUnitId, state);

        when(surveyUnitRepository.findById(surveyUnitId)).thenReturn(Optional.empty());

        // When
        adapter.save(status);

        // Then
        verify(surveyUnitRepository, times(1)).findById(surveyUnitId);
    }

    @Test
    @DisplayName("Should create entity with correct state")
    void testSaveCreatesEntityWithCorrectState() {
        // Given
        String surveyUnitId = "survey-unit-789";
        String state = "MULTIMODE_MOVED";
        SurveyUnitStatus status = new SurveyUnitStatus(surveyUnitId, state);

        when(surveyUnitRepository.findById(surveyUnitId)).thenReturn(Optional.of(surveyUnit));

        // When
        adapter.save(status);

        // Then
        ArgumentCaptor<SurveyUnitStatusEntity> entityCaptor = ArgumentCaptor.forClass(SurveyUnitStatusEntity.class);
        verify(crudRepository).save(entityCaptor.capture());

        assertThat(entityCaptor.getValue().getState()).isEqualTo(state);
    }
}