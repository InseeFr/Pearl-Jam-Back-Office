package fr.insee.pearljam.surveyunit.infrastructure.rest.dto;

import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationRequest;
import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationRequestEmitter;
import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationRequestReason;

import java.util.List;
import java.util.Set;

/**
 * Record representing a CommunicationRequestResponseDto
 * @param reason The reason for the communication request
 * @param emitter The emitter of the communication request
 * @param status The status of the communication request
 */
public record CommunicationRequestResponseDto(
        String communicationTemplateId,
        String campaignId,
        String meshuggahId,
        CommunicationRequestReason reason,
        CommunicationRequestEmitter emitter,
        List<CommunicationRequestStatusDto> status) {

    /**
     * Converts a list of CommunicationRequest models to a set of CommunicationRequestResponseDto.
     * @param requests The set of CommunicationRequest models
     * @return A list of CommunicationRequestResponseDto instances
     */
    public static List<CommunicationRequestResponseDto> fromModel(Set<CommunicationRequest> requests) {
        return requests.stream()
                .map(request -> {
                    List<CommunicationRequestStatusDto> status = request.status().stream()
                            .map(CommunicationRequestStatusDto::fromModel)
                            .toList();
                    return new CommunicationRequestResponseDto(
                            request.meshuggahId(), //The communicationTemplateId field corresponds to the meshuggahId to prevent regression.
                            request.campaignId(),
                            request.meshuggahId(),
                            request.reason(),
                            request.emitter(),
                            status);
                })
                .toList();
    }
}