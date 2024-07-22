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
        Long configurationId = 1L;
        Long creationDate = System.currentTimeMillis();
        CommunicationRequestReason reason = CommunicationRequestReason.REFUSAL;

        // When
        CommunicationRequest communicationRequest = CommunicationRequest.create(configurationId, creationDate, reason);

        // Then
        assertNotNull(communicationRequest);
        assertNull(communicationRequest.id());
        assertEquals(configurationId, communicationRequest.communicationTemplateId());
        assertEquals(reason, communicationRequest.reason());
        assertEquals(CommunicationRequestEmitter.INTERVIEWER, communicationRequest.emitter());
        assertNotNull(communicationRequest.status());
        assertEquals(1, communicationRequest.status().size());
        assertEquals(creationDate, communicationRequest.status().getFirst().date());
    }
}
