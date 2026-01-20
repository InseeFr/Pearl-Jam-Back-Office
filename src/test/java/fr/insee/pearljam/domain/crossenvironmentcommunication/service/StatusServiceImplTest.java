package fr.insee.pearljam.domain.crossenvironmentcommunication.service;

import fr.insee.pearljam.domain.crossenvironmentcommunication.model.SurveyUnitStatus;
import fr.insee.pearljam.domain.crossenvironmentcommunication.port.serverside.SurveyUnitStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusServiceImplTest {

    @Mock
    private SurveyUnitStatusRepository repository;

    private StatusServiceImpl statusService;

    @BeforeEach
    void setUp() {
        statusService = new StatusServiceImpl(repository);
    }

    @Test
    @DisplayName("Should update status with survey unit ID and type")
    void testUpdateStatus() {
        // Given
        String surveyUnitId = "survey-unit-123";
        String type = "QUESTIONNAIRE_INIT";

        // When
        statusService.updateStatus(surveyUnitId, type);

        // Then
        ArgumentCaptor<SurveyUnitStatus> statusCaptor = ArgumentCaptor.forClass(SurveyUnitStatus.class);
        verify(repository, times(1)).save(statusCaptor.capture());

        SurveyUnitStatus capturedStatus = statusCaptor.getValue();
        assertThat(capturedStatus.surveyUnitId()).isEqualTo(surveyUnitId);
        assertThat(capturedStatus.state()).isEqualTo(type);
    }

    @Test
    @DisplayName("Should call repository save method once")
    void testUpdateStatusCallsRepositorySave() {
        // Given
        String surveyUnitId = "survey-unit-456";
        String type = "QUESTIONNAIRE_COMPLETED";

        // When
        statusService.updateStatus(surveyUnitId, type);

        // Then
        verify(repository, times(1)).save(any(SurveyUnitStatus.class));
    }
}