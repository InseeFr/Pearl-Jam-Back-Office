package fr.insee.pearljam.api.crossenvironmentcommunication.broker;

import fr.insee.pearljam.api.crossenvironmentcommunication.broker.dto.EventDto;
import fr.insee.pearljam.api.crossenvironmentcommunication.broker.dto.EventPayloadDto;
import fr.insee.pearljam.api.crossenvironmentcommunication.broker.dto.ModeDto;
import fr.insee.pearljam.domain.crossenvironmentcommunication.port.userside.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusMessageConsumerTest {

    @Mock
    private StatusService statusService;

    private StatusMessageConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new StatusMessageConsumer(statusService);
    }

    @Test
    @DisplayName("Should consume all event types")
    void testShouldConsumeReturnsTrue() {
        // Given
        EventDto.EventTypeEnum eventType = EventDto.EventTypeEnum.QUESTIONNAIRE_INIT;

        // When
        boolean result = consumer.shouldConsume(eventType);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should consume QUESTIONNAIRE_COMPLETED event type")
    void testShouldConsumeQuestionnaireCompleted() {
        // Given
        EventDto.EventTypeEnum eventType = EventDto.EventTypeEnum.QUESTIONNAIRE_COMPLETED;

        // When
        boolean result = consumer.shouldConsume(eventType);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should consume QUESTIONNAIRE_VALIDATED event type")
    void testShouldConsumeQuestionnaireValidated() {
        // Given
        EventDto.EventTypeEnum eventType = EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED;

        // When
        boolean result = consumer.shouldConsume(eventType);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should handle MULTIMODE_MOVED event type")
    void testConsumeMultimodeMoved() {
        // Given
        EventDto.EventTypeEnum eventType = EventDto.EventTypeEnum.MULTIMODE_MOVED;
        String interrogationId = "survey-unit-999";
        ModeDto mode = ModeDto.CAPI;
        EventPayloadDto payload = new EventPayloadDto(interrogationId, mode);

        // When
        consumer.consume(eventType, payload);

        // Then
        verify(statusService, times(1)).updateStatus(interrogationId, "MULTIMODE_MOVED");
    }

    @Test
    @DisplayName("Should handle QUESTIONNAIRE_LEAF_STATES_UPDATED event type")
    void testConsumeQuestionnaireLeafStatesUpdated() {
        // Given
        EventDto.EventTypeEnum eventType = EventDto.EventTypeEnum.QUESTIONNAIRE_LEAF_STATES_UPDATED;
        String interrogationId = "survey-unit-111";
        ModeDto mode = ModeDto.CAPI;
        EventPayloadDto payload = new EventPayloadDto(interrogationId, mode);

        // When
        consumer.consume(eventType, payload);

        // Then
        verify(statusService, times(1)).updateStatus(interrogationId, "QUESTIONNAIRE_LEAF_STATES_UPDATED");
    }

    @Test
    @DisplayName("Should call statusService updateStatus with correct parameters")
    void testConsumeCallsUpdateStatus() {
        // Given
        EventDto.EventTypeEnum eventType = EventDto.EventTypeEnum.QUESTIONNAIRE_INIT;
        String interrogationId = "survey-unit-123";
        ModeDto mode = ModeDto.CAPI;
        EventPayloadDto payload = new EventPayloadDto(interrogationId, mode);

        // When
        consumer.consume(eventType, payload);

        // Then
        verify(statusService, times(1)).updateStatus(interrogationId, eventType.getValue());
    }

    @Test
    @DisplayName("Should extract interrogation ID from payload")
    void testConsumeExtractsInterrogationId() {
        // Given
        EventDto.EventTypeEnum eventType = EventDto.EventTypeEnum.QUESTIONNAIRE_COMPLETED;
        String interrogationId = "survey-unit-456";
        ModeDto mode = ModeDto.CAPI;
        EventPayloadDto payload = new EventPayloadDto(interrogationId, mode);

        // When
        consumer.consume(eventType, payload);

        // Then
        verify(statusService).updateStatus(eq(interrogationId), anyString());
    }

    @Test
    @DisplayName("Should pass event type value as state")
    void testConsumePassesEventTypeValue() {
        // Given
        EventDto.EventTypeEnum eventType = EventDto.EventTypeEnum.QUESTIONNAIRE_VALIDATED;
        String interrogationId = "survey-unit-789";
        ModeDto mode = ModeDto.CAPI;
        EventPayloadDto payload = new EventPayloadDto(interrogationId, mode);

        // When
        consumer.consume(eventType, payload);

        // Then
        verify(statusService).updateStatus(anyString(), eq(eventType.getValue()));
    }
}