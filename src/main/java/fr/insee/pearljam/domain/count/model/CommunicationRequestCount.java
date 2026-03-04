package fr.insee.pearljam.domain.count.model;

public record CommunicationRequestCount(
        String entityId,
        Long noticeCount,
        Long reminderCount) {
}