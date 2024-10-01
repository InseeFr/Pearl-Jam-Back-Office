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
     * Create a communication request for meshuggah
     * @param communicationTemplateId communication configuration id
     * @param creationTimestamp creation date of the communication request (coming from the front)
     * @param readyTimestamp ready timestamp of the communication request (when does the communication request is created in the back)
     * @param reason reason why the communication request is created
     * @return {@link CommunicationRequest} communication request object
     */
    public static CommunicationRequest create(Long communicationTemplateId, Long creationTimestamp, Long readyTimestamp, CommunicationRequestReason reason) {
        List<CommunicationRequestStatus> status = new ArrayList<>();
        status.add(CommunicationRequestStatus.create(creationTimestamp, CommunicationStatusType.INITIATED));
        status.add(CommunicationRequestStatus.create(readyTimestamp, CommunicationStatusType.READY));
        return new CommunicationRequest(null, communicationTemplateId, reason, CommunicationRequestEmitter.INTERVIEWER, status);
    }

    /**
     * Create a communication request for meshuggah
     * @param communicationTemplateId communication configuration id
     * @param creationTimestamp creation date of the communication request (coming from the front)
     * @param readyTimestamp ready timestamp of the communication request (when does the communication request is created in the back)
     * @param reason reason why the communication request is created
     * @return {@link CommunicationRequest} communication request object
     */
    public static CommunicationRequest createCancelled(Long communicationTemplateId, Long creationTimestamp, Long readyTimestamp, CommunicationRequestReason reason) {
        List<CommunicationRequestStatus> status = new ArrayList<>();
        status.add(CommunicationRequestStatus.create(creationTimestamp, CommunicationStatusType.INITIATED));
        status.add(CommunicationRequestStatus.create(readyTimestamp, CommunicationStatusType.CANCELLED));
        return new CommunicationRequest(null, communicationTemplateId, reason, CommunicationRequestEmitter.INTERVIEWER, status);
    }
}
