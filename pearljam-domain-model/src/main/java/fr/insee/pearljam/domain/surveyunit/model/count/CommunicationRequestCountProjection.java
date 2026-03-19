package fr.insee.pearljam.domain.surveyunit.model.count;

public record CommunicationRequestCountProjection(
        String entityId,
        Long noticeCount,
        Long reminderCount) {
}