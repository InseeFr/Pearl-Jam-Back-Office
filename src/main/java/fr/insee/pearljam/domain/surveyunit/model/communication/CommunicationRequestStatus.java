package fr.insee.pearljam.domain.surveyunit.model.communication;

public record CommunicationRequestStatus(
        Long id,
        Long date,
        CommunicationStatusType status) {
}
