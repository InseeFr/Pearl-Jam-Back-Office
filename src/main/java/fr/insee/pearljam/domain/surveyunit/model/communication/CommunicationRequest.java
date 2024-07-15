package fr.insee.pearljam.domain.surveyunit.model.communication;

import java.util.List;

public record CommunicationRequest(
        Long id,
        String messhugahId,
        CommunicationRequestType type,
        CommunicationRequestReason reason,
        CommunicationRequestMedium medium,
        CommunicationRequestEmiter emiter,
        List<CommunicationRequestStatus> status) {
}
