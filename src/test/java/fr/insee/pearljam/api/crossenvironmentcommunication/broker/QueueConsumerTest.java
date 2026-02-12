package fr.insee.pearljam.api.crossenvironmentcommunication.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pearljam.api.crossenvironmentcommunication.broker.debezium.CDCEnvelope;
import fr.insee.pearljam.api.crossenvironmentcommunication.broker.debezium.Value;
import fr.insee.pearljam.api.crossenvironmentcommunication.broker.dto.EventDto;
import fr.insee.pearljam.api.crossenvironmentcommunication.broker.dto.EventPayloadDto;
import fr.insee.pearljam.api.crossenvironmentcommunication.broker.dto.ModeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueConsumerTest {

    @Mock
    private MessageConsumer messageConsumer;

    private QueueConsumer queueConsumer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        queueConsumer = new QueueConsumer(List.of(messageConsumer));
    }

    @Test
    @DisplayName("Should process valid CDC envelope and call consumer")
    void testListenWithValidEnvelope() throws JsonProcessingException {
        // Given
        when(messageConsumer.shouldConsume(any(EventDto.EventTypeEnum.class))).thenReturn(true);

        EventPayloadDto payload = new EventPayloadDto("survey-unit-123", ModeDto.CAPI);
        EventDto event = new EventDto(
                EventDto.EventTypeEnum.QUESTIONNAIRE_INIT,
                EventDto.AggregateTypeEnum.QUESTIONNAIRE,
                payload
        );
        String eventJson = objectMapper.writeValueAsString(event);

        Value value = new Value("1", eventJson, 1234567890L);
        CDCEnvelope envelope = new CDCEnvelope(null, value, null, "c", 1234567890L, null);
        String envelopeJson = objectMapper.writeValueAsString(envelope);

        // When
        queueConsumer.listen(envelopeJson, "key-json");

        // Then
        verify(messageConsumer, times(1)).shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        verify(messageConsumer, times(1)).consume(eq(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT), any(EventPayloadDto.class));
    }

    @Test
    @DisplayName("Should not call consumer when shouldConsume returns false")
    void testListenWithConsumerShouldNotConsume() throws JsonProcessingException {
        // Given
        when(messageConsumer.shouldConsume(any(EventDto.EventTypeEnum.class))).thenReturn(false);

        EventPayloadDto payload = new EventPayloadDto("survey-unit-456", ModeDto.CAPI);
        EventDto event = new EventDto(
                EventDto.EventTypeEnum.QUESTIONNAIRE_COMPLETED,
                EventDto.AggregateTypeEnum.QUESTIONNAIRE,
                payload
        );
        String eventJson = objectMapper.writeValueAsString(event);

        Value value = new Value("2", eventJson, 1234567890L);
        CDCEnvelope envelope = new CDCEnvelope(null, value, null, "c", 1234567890L, null);
        String envelopeJson = objectMapper.writeValueAsString(envelope);

        // When
        queueConsumer.listen(envelopeJson, "key-json");

        // Then
        verify(messageConsumer, times(1)).shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_COMPLETED);
        verify(messageConsumer, never()).consume(any(EventDto.EventTypeEnum.class), any(EventPayloadDto.class));
    }

    @Test
    @DisplayName("Should handle null envelope")
    void testListenWithNullEnvelope() throws JsonProcessingException {
        // Given
        String envelopeJson = "null";

        // When
        queueConsumer.listen(envelopeJson, "key-json");

        // Then
        verify(messageConsumer, never()).shouldConsume(any(EventDto.EventTypeEnum.class));
        verify(messageConsumer, never()).consume(any(EventDto.EventTypeEnum.class), any(EventPayloadDto.class));
    }

    @Test
    @DisplayName("Should handle envelope with null after field")
    void testListenWithNullAfterField() throws JsonProcessingException {
        // Given
        CDCEnvelope envelope = new CDCEnvelope(null, null, null, "c", 1234567890L, null);
        String envelopeJson = objectMapper.writeValueAsString(envelope);

        // When
        queueConsumer.listen(envelopeJson, "key-json");

        // Then
        verify(messageConsumer, never()).shouldConsume(any(EventDto.EventTypeEnum.class));
        verify(messageConsumer, never()).consume(any(EventDto.EventTypeEnum.class), any(EventPayloadDto.class));
    }

    @Test
    @DisplayName("Should handle invalid JSON gracefully")
    void testListenWithInvalidJson() {
        // Given
        String invalidJson = "{ invalid json }";

        // When
        queueConsumer.listen(invalidJson, "key-json");

        // Then
        verify(messageConsumer, never()).shouldConsume(any(EventDto.EventTypeEnum.class));
        verify(messageConsumer, never()).consume(any(EventDto.EventTypeEnum.class), any(EventPayloadDto.class));
    }

    @Test
    @DisplayName("Should call all consumers that should consume")
    void testListenCallsAllConsumers() throws JsonProcessingException {
        // Given
        MessageConsumer consumer1 = mock(MessageConsumer.class);
        MessageConsumer consumer2 = mock(MessageConsumer.class);
        MessageConsumer consumer3 = mock(MessageConsumer.class);

        when(consumer1.shouldConsume(any(EventDto.EventTypeEnum.class))).thenReturn(true);
        when(consumer2.shouldConsume(any(EventDto.EventTypeEnum.class))).thenReturn(false);
        when(consumer3.shouldConsume(any(EventDto.EventTypeEnum.class))).thenReturn(true);

        QueueConsumer queueConsumerMultiple = new QueueConsumer(List.of(consumer1, consumer2, consumer3));

        EventPayloadDto payload = new EventPayloadDto("survey-unit-789", ModeDto.CAPI);
        EventDto event = new EventDto(
                EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED,
                EventDto.AggregateTypeEnum.QUESTIONNAIRE,
                payload
        );
        String eventJson = objectMapper.writeValueAsString(event);

        Value value = new Value("3", eventJson, 1234567890L);
        CDCEnvelope envelope = new CDCEnvelope(null, value, null, "c", 1234567890L, null);
        String envelopeJson = objectMapper.writeValueAsString(envelope);

        // When
        queueConsumerMultiple.listen(envelopeJson, "key-json");

        // Then
        verify(consumer1, times(1)).shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED);
        verify(consumer1, times(1)).consume(eq(EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED), any(EventPayloadDto.class));

        verify(consumer2, times(1)).shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED);
        verify(consumer2, never()).consume(any(EventDto.EventTypeEnum.class), any(EventPayloadDto.class));

        verify(consumer3, times(1)).shouldConsume(EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED);
        verify(consumer3, times(1)).consume(eq(EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED), any(EventPayloadDto.class));
    }
}