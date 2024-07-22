package fr.insee.pearljam.domain.surveyunit.model.communication;

public record CommunicationRequestStatus(
        Long id,
        Long date,
        CommunicationStatusType status) {

    /**
     * Create a CommunicationRequestStatus
     * @param timestamp status creation date
     * @return {@link CommunicationRequestStatus} communication request status object
     */
    public static CommunicationRequestStatus create(Long timestamp) {
        return new CommunicationRequestStatus(null, timestamp, CommunicationStatusType.INITIATED);
    }
}
