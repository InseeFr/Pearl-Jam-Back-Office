package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestEmitter;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestReason;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommunicationRequestTest {

    @Test
    void testCreateCommunicationRequest() {
        // Given
        Long communicationTemplateId = 1L;
        Long creationDate = System.currentTimeMillis();
        Long readyDate = System.currentTimeMillis();
        CommunicationRequestReason reason = CommunicationRequestReason.REFUSAL;

        // When
        CommunicationRequest communicationRequest = CommunicationRequest.create(communicationTemplateId, creationDate, readyDate, reason);

        // Then
        assertNotNull(communicationRequest);
        assertNull(communicationRequest.id());
        assertEquals(communicationTemplateId, communicationRequest.communicationTemplateId());
        assertEquals(reason, communicationRequest.reason());
        assertEquals(CommunicationRequestEmitter.INTERVIEWER, communicationRequest.emitter());
        assertNotNull(communicationRequest.status());
        assertEquals(2, communicationRequest.status().size());
        assertEquals(creationDate, communicationRequest.status().getFirst().date());
        assertEquals(readyDate, communicationRequest.status().getLast().date());
    }
}
