package fr.insee.pearljam.surveyunit.infrastructure.rest.dto;

import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationRequestReason;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a communication request.
 *
 * <p>This class represents the data required to create a
 * communication request, including the configuration ID, the
 * creationTimestamp, and the reason for the request.</p>
 *
 * @param communicationTemplateId communicationTemplateId : AKA meshuggahId -> used for compatibility with interviewer app
 * @param creationTimestamp the creationTimestamp of the request, must not be null
 * @param reason the reason for the communication request, must not be null
 */
public record CommunicationRequestCreateDto(
    @NotNull
    String communicationTemplateId,
    @NotNull
    Long creationTimestamp,
    @NotNull
    CommunicationRequestReason reason) {

}
