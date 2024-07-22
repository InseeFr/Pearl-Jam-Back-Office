package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.domain.surveyunit.model.communication.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO for creating a communication request.
 *
 * <p>This class represents the data required to create a
 * communication request, including the configuration ID, the
 * creationTimestamp, and the reason for the request.</p>
 *
 * @param communicationTemplateId the configuration ID, must not be null
 * @param creationTimestamp the creationTimestamp of the request, must not be null
 * @param reason the reason for the communication request, must not be null
 */
public record CommunicationRequestCreateDto(
    @NotNull
    Long communicationTemplateId,
    @NotNull
    Long creationTimestamp,
    @NotNull
    CommunicationRequestReason reason) {

    /**
     * Converts a list of communication request DTOs into a list of communication request models.
     *
     * @param requests the list of communication request DTOs
     * @return the list of communication request models
     */
    public static List<CommunicationRequest> toModel(List<CommunicationRequestCreateDto> requests) {
        return requests.stream()
                .map(request ->
                        CommunicationRequest.create(request.communicationTemplateId(), request.creationTimestamp(), request.reason()))
                .toList();
    }
}
