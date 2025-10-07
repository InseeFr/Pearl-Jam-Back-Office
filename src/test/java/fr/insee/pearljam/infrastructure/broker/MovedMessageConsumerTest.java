package fr.insee.pearljam.infrastructure.broker;

import fr.insee.pearljam.domain.surveyunit.port.userside.MovedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovedMessageConsumerTest {

    @Mock
    private MovedService movedService;

    private MovedMessageConsumer sut;

    @BeforeEach
    void setUp() {
        sut = new MovedMessageConsumer(movedService);
    }

    @Test
    void shouldConsume_returns_true_for_MULTIMODE_MOVED() {
        assertTrue(sut.shouldConsume("MULTIMODE_MOVED"));
    }

    @Test
    void shouldConsume_returns_true_for_multimode_moved_case_insensitive() {
        assertTrue(sut.shouldConsume("multimode_moved"));
    }

    @Test
    void shouldConsume_returns_false_for_other_types() {
        assertFalse(sut.shouldConsume("QUESTIONNAIRE_INIT"));
        assertFalse(sut.shouldConsume("QUESTIONNAIRE_COMPLETED"));
        assertFalse(sut.shouldConsume("OTHER_TYPE"));
    }

    @Test
    void consume_delegates_to_movedService() {
        String type = "MULTIMODE_MOVED";
        String interrogationId = "ABC-123";

        BrokerMessage.Payload payload = mock(BrokerMessage.Payload.class);
        when(payload.interrogationId()).thenReturn(interrogationId);

        sut.consume(type, payload);

        verify(movedService, times(1)).updateMovedSurveyUnit(interrogationId);
        verifyNoMoreInteractions(movedService);
    }
}