package fr.insee.pearljam.surveyunit.domain.model.communication;

public record CommunicationRequestStatus(
        Long id,
        Long date,
        CommunicationStatusType status) {

    /**
     * Create a CommunicationRequestStatus
     * @param timestamp status creation date
     * @param statusType status type
     * @return {@link CommunicationRequestStatus} communication request status object
     */
    public static CommunicationRequestStatus create(Long timestamp, CommunicationStatusType statusType) {
        return new CommunicationRequestStatus(null, timestamp, statusType);
    }
}
