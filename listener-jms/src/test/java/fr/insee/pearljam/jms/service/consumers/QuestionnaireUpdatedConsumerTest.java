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
class QuestionnaireUpdatedConsumerTest {

    @Mock
    private StatusService statusService;

    private QuestionnaireUpdatedConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new QuestionnaireUpdatedConsumer(statusService);
    }

    @Test
    @DisplayName("Should call statusService when event type is QUESTIONNAIRE_UPDATED")
    void shouldCallStatusServiceWhenEventTypeIsQuestionnaireUpdated() {
        // Given
        String interrogationId = "SU-001";
        EventDto eventDto = createEventDto(EventDto.EventTypeEnum.QUESTIONNAIRE_UPDATED, interrogationId);

        // When
        consumer.consume(eventDto);

        // Then
        verify(statusService, times(1)).updateStatus(interrogationId, "QUESTIONNAIRE_UPDATED");
    }

    @Test
    @DisplayName("Should not call statusService when event type is not QUESTIONNAIRE_UPDATED")
    void shouldNotCallStatusServiceWhenEventTypeIsNotQuestionnaireUpdated() {
        // Given
        EventDto eventDto = createEventDto(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT, "SU-001");

        // When
        consumer.consume(eventDto);

        // Then
        verify(statusService, never()).updateStatus(anyString(), anyString());
    }

    @Test
    @DisplayName("Should not call statusService when event type is QUESTIONNAIRE_VALIDATED")
    void shouldNotCallStatusServiceWhenEventTypeIsQuestionnaireValidated() {
        // Given
        EventDto eventDto = createEventDto(EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED, "SU-001");

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
