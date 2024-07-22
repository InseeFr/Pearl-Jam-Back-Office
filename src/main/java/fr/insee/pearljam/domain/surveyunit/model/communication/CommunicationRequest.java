package fr.insee.pearljam.domain.surveyunit.model.communication;

import java.util.ArrayList;
import java.util.List;

public record CommunicationRequest(
        Long id,
        Long communicationTemplateId,
        CommunicationRequestReason reason,
        CommunicationRequestEmitter emitter,
        List<CommunicationRequestStatus> status) {

    /**
     * Create a communication request for messhugah
     * @param communicationTemplateId communication configuration id
     * @param creationTimestamp creation date of the communication request
     * @param reason reason why the communication request is created
     * @return {@link CommunicationRequest} communication request object
     */
    public static CommunicationRequest create(Long communicationTemplateId, Long creationTimestamp, CommunicationRequestReason reason) {
        List<CommunicationRequestStatus> status = new ArrayList<>();
        status.add(CommunicationRequestStatus.create(creationTimestamp));
        return new CommunicationRequest(null, communicationTemplateId, reason, CommunicationRequestEmitter.INTERVIEWER, status);
    }
}
