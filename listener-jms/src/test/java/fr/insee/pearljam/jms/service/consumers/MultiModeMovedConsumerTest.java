package fr.insee.pearljam.jms.service.consumers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.modelefiliere.EventPayloadDto;
import fr.insee.pearljam.domain.surveyunit.port.userside.MovedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultiModeMovedConsumerTest {

    @Mock
    private MovedService movedService;

    private MultiModeMovedConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new MultiModeMovedConsumer(movedService);
    }

    @Test
    @DisplayName("Should call movedService when event type is MULTIMODE_MOVED")
    void shouldCallMovedServiceWhenEventTypeIsMultimodeMoved() {
        // Given
        String interrogationId = "SU-001";
        EventDto eventDto = createEventDto(EventDto.EventTypeEnum.MULTIMODE_MOVED, interrogationId);

        // When
        consumer.consume(eventDto);

        // Then
        verify(movedService, times(1)).updateMovedSurveyUnit(interrogationId);
    }

    @Test
    @DisplayName("Should not call movedService when event type is not MULTIMODE_MOVED")
    void shouldNotCallMovedServiceWhenEventTypeIsNotMultimodeMoved() {
        // Given
        EventDto eventDto = createEventDto(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT, "SU-001");

        // When
        consumer.consume(eventDto);

        // Then
        verify(movedService, never()).updateMovedSurveyUnit(anyString());
    }

    @Test
    @DisplayName("Should not call movedService when event type is QUESTIONNAIRE_COMPLETED")
    void shouldNotCallMovedServiceWhenEventTypeIsQuestionnaireCompleted() {
        // Given
        EventDto eventDto = createEventDto(EventDto.EventTypeEnum.QUESTIONNAIRE_COMPLETED, "SU-001");

        // When
        consumer.consume(eventDto);

        // Then
        verify(movedService, never()).updateMovedSurveyUnit(anyString());
    }

    private EventDto createEventDto(EventDto.EventTypeEnum eventType, String interrogationId) {
        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId(interrogationId);

        EventDto eventDto = new EventDto();
        eventDto.setEventType(eventType);
        eventDto.setPayload(payload);

        return eventDto;
    }
}
