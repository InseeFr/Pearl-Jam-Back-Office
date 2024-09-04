package fr.insee.pearljam.domain.campaign.model.communication;


/**
 * A class representing the communication template of a visibility.
 *
 * @param id           The unique identifier of the communication template
 * @param meshuggahId  The identifier for Messhugah
 * @param medium       The medium of communication
 * @param type         The type of communication
 */
public record CommunicationTemplate(
        Long id,
        String meshuggahId,
        CommunicationMedium medium,
        CommunicationType type
) {
}