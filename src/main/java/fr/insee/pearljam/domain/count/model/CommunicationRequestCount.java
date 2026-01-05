package fr.insee.pearljam.domain.count.model;

public record CommunicationRequestCount(
        String campaignId,
        Long noticeCount,
        Long reminderCount) {
}