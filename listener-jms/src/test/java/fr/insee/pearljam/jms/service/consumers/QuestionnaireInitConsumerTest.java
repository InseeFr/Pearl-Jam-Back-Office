package fr.insee.pearljam.jms.service.consumers;

import fr.insee.modelefiliere.EventDto;
import fr.insee.modelefiliere.EventPayloadDto;
import fr.insee.pearljam.domain.surveyunit.port.userside.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionnaireInitConsumerTest {

    @Mock
    private StatusService statusService;

    private QuestionnaireInitConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new QuestionnaireInitConsumer(statusService);
    }

    @Test
    @DisplayName("Should call statusService when event type is QUESTIONNAIRE_INIT")
    void shouldCallStatusServiceWhenEventTypeIsQuestionnaireInit() {
        // Given
        String interrogationId = "SU-001";
        EventDto eventDto = createEventDto(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT, interrogationId);

        // When
        consumer.consume(eventDto);

        // Then
        verify(statusService, times(1)).updateStatus(interrogationId, "QUESTIONNAIRE_INIT");
    }

    @Test
    @DisplayName("Should not call statusService when event type is not QUESTIONNAIRE_INIT")
    void shouldNotCallStatusServiceWhenEventTypeIsNotQuestionnaireInit() {
        // Given
        EventDto eventDto = createEventDto(EventDto.EventTypeEnum.QUESTIONNAIRE_COMPLETED, "SU-001");

        // When
        consumer.consume(eventDto);

        // Then
        verify(statusService, never()).updateStatus(anyString(), anyString());
    }

    @Test
    @DisplayName("Should not call statusService when event type is MULTIMODE_MOVED")
    void shouldNotCallStatusServiceWhenEventTypeIsMultimodeMoved() {
        // Given
        EventDto eventDto = createEventDto(EventDto.EventTypeEnum.MULTIMODE_MOVED, "SU-001");

        // When
        consumer.consume(eventDto);

        // Then
        verify(statusService, never()).updateStatus(anyString(), anyString());
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
