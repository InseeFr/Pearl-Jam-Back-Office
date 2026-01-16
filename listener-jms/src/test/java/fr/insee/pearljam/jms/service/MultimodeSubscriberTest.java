package fr.insee.pearljam.jms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.modelefiliere.EventDto;
import fr.insee.modelefiliere.EventPayloadDto;
import fr.insee.pearljam.infrastructure.db.events.InboxDB;
import fr.insee.pearljam.infrastructure.db.events.InboxJpaRepository;
import fr.insee.pearljam.jms.configuration.MultimodeProperties;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MultimodeSubscriberTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MultimodeProperties multimodeProperties;

    @Mock
    private InboxJpaRepository inboxRepository;

    @Mock
    private EventConsumer eventConsumer1;

    @Mock
    private EventConsumer eventConsumer2;

    @Mock
    private TextMessage message;

    private MultimodeSubscriber subscriber;

    @BeforeEach
    void setUp() {
        List<EventConsumer> consumers = Arrays.asList(eventConsumer1, eventConsumer2);
        subscriber = new MultimodeSubscriber(objectMapper, multimodeProperties, consumers, inboxRepository);
    }

    @Test
    @DisplayName("Should save message to inbox and dispatch to consumers")
    void shouldSaveMessageToInboxAndDispatchToConsumers() throws Exception {
        // Given
        UUID correlationId = UUID.randomUUID();
        String jsonMessage = "{\"correlationId\":\"" + correlationId + "\",\"eventType\":\"QUESTIONNAIRE_INIT\"}";

        EventDto eventDto = createEventDto(correlationId, EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        when(message.getBody(String.class)).thenReturn(jsonMessage);
        when(multimodeProperties.getTopic()).thenReturn("test-topic");
        when(objectMapper.readValue(jsonMessage, EventDto.class)).thenReturn(eventDto);
        when(objectMapper.valueToTree(eventDto)).thenReturn(objectNode);
        when(inboxRepository.existsById(correlationId)).thenReturn(false);
        when(inboxRepository.save(any(InboxDB.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        subscriber.onMessage(message);

        // Then
        ArgumentCaptor<InboxDB> inboxCaptor = ArgumentCaptor.forClass(InboxDB.class);
        verify(inboxRepository).save(inboxCaptor.capture());

        InboxDB savedInbox = inboxCaptor.getValue();
        assertEquals(correlationId, savedInbox.getId());

        verify(eventConsumer1).consume(eventDto);
        verify(eventConsumer2).consume(eventDto);
    }

    @Test
    @DisplayName("Should skip message without correlationId")
    void shouldSkipMessageWithoutCorrelationId() throws Exception {
        // Given
        String jsonMessage = "{\"eventType\":\"QUESTIONNAIRE_INIT\"}";

        EventDto eventDto = new EventDto();
        eventDto.setCorrelationId(null);
        eventDto.setEventType(EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);

        when(message.getBody(String.class)).thenReturn(jsonMessage);
        when(multimodeProperties.getTopic()).thenReturn("test-topic");
        when(objectMapper.readValue(jsonMessage, EventDto.class)).thenReturn(eventDto);

        // When
        subscriber.onMessage(message);

        // Then
        verify(inboxRepository, never()).save(any());
        verify(eventConsumer1, never()).consume(any());
        verify(eventConsumer2, never()).consume(any());
    }

    @Test
    @DisplayName("Should ignore duplicate message")
    void shouldIgnoreDuplicateMessage() throws Exception {
        // Given
        UUID correlationId = UUID.randomUUID();
        String jsonMessage = "{\"correlationId\":\"" + correlationId + "\",\"eventType\":\"QUESTIONNAIRE_INIT\"}";

        EventDto eventDto = createEventDto(correlationId, EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);

        when(message.getBody(String.class)).thenReturn(jsonMessage);
        when(multimodeProperties.getTopic()).thenReturn("test-topic");
        when(objectMapper.readValue(jsonMessage, EventDto.class)).thenReturn(eventDto);
        when(inboxRepository.existsById(correlationId)).thenReturn(true);

        // When
        subscriber.onMessage(message);

        // Then
        verify(inboxRepository, never()).save(any());
        verify(eventConsumer1, never()).consume(any());
        verify(eventConsumer2, never()).consume(any());
    }

    @Test
    @DisplayName("Should continue with other consumers when one fails")
    void shouldContinueWithOtherConsumersWhenOneFails() throws Exception {
        // Given
        UUID correlationId = UUID.randomUUID();
        String jsonMessage = "{\"correlationId\":\"" + correlationId + "\",\"eventType\":\"QUESTIONNAIRE_INIT\"}";

        EventDto eventDto = createEventDto(correlationId, EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        when(message.getBody(String.class)).thenReturn(jsonMessage);
        when(multimodeProperties.getTopic()).thenReturn("test-topic");
        when(objectMapper.readValue(jsonMessage, EventDto.class)).thenReturn(eventDto);
        when(objectMapper.valueToTree(eventDto)).thenReturn(objectNode);
        when(inboxRepository.existsById(correlationId)).thenReturn(false);
        when(inboxRepository.save(any(InboxDB.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doThrow(new RuntimeException("Consumer 1 failed")).when(eventConsumer1).consume(any());

        // When
        subscriber.onMessage(message);

        // Then
        verify(eventConsumer1).consume(eventDto);
        verify(eventConsumer2).consume(eventDto);
        verify(inboxRepository).save(any(InboxDB.class));
    }

    @Test
    @DisplayName("Should throw exception when message parsing fails")
    void shouldThrowExceptionWhenMessageParsingFails() throws Exception {
        // Given
        String invalidJson = "invalid json";

        when(message.getBody(String.class)).thenReturn(invalidJson);
        when(multimodeProperties.getTopic()).thenReturn("test-topic");
        when(objectMapper.readValue(invalidJson, EventDto.class)).thenThrow(new RuntimeException("Parse error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> subscriber.onMessage(message));

        verify(inboxRepository, never()).save(any());
        verify(eventConsumer1, never()).consume(any());
    }

    @Test
    @DisplayName("Should handle empty consumers list")
    void shouldHandleEmptyConsumersList() throws Exception {
        // Given
        MultimodeSubscriber subscriberWithNoConsumers = new MultimodeSubscriber(
                objectMapper, multimodeProperties, Collections.emptyList(), inboxRepository);

        UUID correlationId = UUID.randomUUID();
        String jsonMessage = "{\"correlationId\":\"" + correlationId + "\",\"eventType\":\"QUESTIONNAIRE_INIT\"}";

        EventDto eventDto = createEventDto(correlationId, EventDto.EventTypeEnum.QUESTIONNAIRE_INIT);
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        when(message.getBody(String.class)).thenReturn(jsonMessage);
        when(multimodeProperties.getTopic()).thenReturn("test-topic");
        when(objectMapper.readValue(jsonMessage, EventDto.class)).thenReturn(eventDto);
        when(objectMapper.valueToTree(eventDto)).thenReturn(objectNode);
        when(inboxRepository.existsById(correlationId)).thenReturn(false);
        when(inboxRepository.save(any(InboxDB.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        subscriberWithNoConsumers.onMessage(message);

        // Then
        verify(inboxRepository).save(any(InboxDB.class));
    }

    @Test
    @DisplayName("Should process different event types")
    void shouldProcessDifferentEventTypes() throws Exception {
        // Given
        UUID correlationId = UUID.randomUUID();
        EventDto eventDto = createEventDto(correlationId, EventDto.EventTypeEnum.MULTIMODE_MOVED);
        String jsonMessage = "{\"correlationId\":\"" + correlationId + "\",\"eventType\":\"MULTIMODE_MOVED\"}";
        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        when(message.getBody(String.class)).thenReturn(jsonMessage);
        when(multimodeProperties.getTopic()).thenReturn("test-topic");
        when(objectMapper.readValue(jsonMessage, EventDto.class)).thenReturn(eventDto);
        when(objectMapper.valueToTree(eventDto)).thenReturn(objectNode);
        when(inboxRepository.existsById(correlationId)).thenReturn(false);
        when(inboxRepository.save(any(InboxDB.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        subscriber.onMessage(message);

        // Then
        verify(inboxRepository).save(any(InboxDB.class));
        verify(eventConsumer1).consume(eventDto);
        verify(eventConsumer2).consume(eventDto);
    }

    private EventDto createEventDto(UUID correlationId, EventDto.EventTypeEnum eventType) {
        EventPayloadDto payload = new EventPayloadDto();
        payload.setInterrogationId("SU-001");

        EventDto eventDto = new EventDto();
        eventDto.setCorrelationId(correlationId);
        eventDto.setEventType(eventType);
        eventDto.setPayload(payload);

        return eventDto;
    }
}
