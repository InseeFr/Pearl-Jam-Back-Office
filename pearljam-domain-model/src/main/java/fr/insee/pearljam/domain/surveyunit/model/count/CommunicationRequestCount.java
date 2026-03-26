package fr.insee.pearljam.domain.surveyunit.model.count;

public record CommunicationRequestCount(
        String entityId,
        Long noticeCount,
        Long reminderCount) {
}