package fr.insee.pearljam.domain.surveyunit.model.communication;

public record CommunicationHistoryDto(
        Long date,
        String type,
        String reason) {
}
