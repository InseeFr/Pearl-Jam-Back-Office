package fr.insee.pearljam.domain.campaign.model.communication;


/**
 * A class representing the communication template of a visibility.
 *
 * @param meshuggahId  The identifier for Messhugah
 * @param medium       The medium of communication
 * @param type         The type of communication
 */
public record CommunicationTemplate(
        String campaignId,
        String meshuggahId,
        CommunicationMedium medium,
        CommunicationType type
) {
}