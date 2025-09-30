package fr.insee.pearljam.infrastructure.broker;

import fr.insee.pearljam.domain.surveyunit.port.state.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusMessageConsumerTest {

    @Mock
    private StatusService statusService;

    private StatusMessageConsumer sut;

    @BeforeEach
    void setUp() {
        sut = new StatusMessageConsumer(statusService);
    }

    @Test
    void shouldConsume_returns_true_for_QUESTIONNAIRE_INIT() {
        assertTrue(sut.shouldConsume("QUESTIONNAIRE_INIT"));
    }

    @Test
    void shouldConsume_returns_true_for_QUESTIONNAIRE_COMPLETED() {
        assertTrue(sut.shouldConsume("QUESTIONNAIRE_COMPLETED"));
    }


    @Test
    void consume_delegates_to_statusService_for_INIT() {
        String type = "QUESTIONNAIRE_INIT";
        String interrogationId = "ABC-123";

        BrokerMessage.Payload payload = mock(BrokerMessage.Payload.class);
        when(payload.interrogationId()).thenReturn(interrogationId);

        sut.consume(type, payload);

        verify(statusService, times(1)).updateStatus(interrogationId, type);
        verifyNoMoreInteractions(statusService);
    }

    @Test
    void consume_delegates_to_statusService_for_COMPLETED() {
        String type = "QUESTIONNAIRE_COMPLETED";
        String interrogationId = "XYZ-999";

        BrokerMessage.Payload payload = mock(BrokerMessage.Payload.class);
        when(payload.interrogationId()).thenReturn(interrogationId);

        sut.consume(type, payload);

        verify(statusService).updateStatus(interrogationId, type);
        verifyNoMoreInteractions(statusService);
    }
}