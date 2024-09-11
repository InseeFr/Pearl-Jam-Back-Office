package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestEmitter;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestReason;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(communicationRequest).isNotNull();
        assertThat(communicationRequest.id()).isNull();
        assertThat(communicationTemplateId).isEqualTo(communicationRequest.communicationTemplateId());
        assertThat(reason).isEqualTo(communicationRequest.reason());
        assertThat(communicationRequest.emitter()).isEqualTo(CommunicationRequestEmitter.INTERVIEWER);
        assertThat(communicationRequest.status()).hasSize(2);
        assertThat(communicationRequest.status().getFirst().date()).isEqualTo(creationDate);
        assertThat(communicationRequest.status().getLast().date()).isEqualTo(readyDate);
    }
}
