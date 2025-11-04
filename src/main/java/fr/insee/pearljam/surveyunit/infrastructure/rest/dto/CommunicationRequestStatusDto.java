package fr.insee.pearljam.surveyunit.infrastructure.rest.dto;

import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationRequestStatus;
import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationStatusType;

/**
 * Record representing a CommunicationRequestStatusDto
 *
 * @param date   The date of the communication request status
 * @param status The status type of the communication request
 */
public record CommunicationRequestStatusDto(
        Long date,
        CommunicationStatusType status) {

    /**
     * Converts a CommunicationRequestStatus model to a CommunicationRequestStatusDto.
     *
     * @param requestStatus The CommunicationRequestStatus model to convert.
     * @return A new CommunicationRequestStatusDto instance.
     */
    public static CommunicationRequestStatusDto fromModel(CommunicationRequestStatus requestStatus) {
        return new CommunicationRequestStatusDto(requestStatus.date(), requestStatus.status());
    }
}
