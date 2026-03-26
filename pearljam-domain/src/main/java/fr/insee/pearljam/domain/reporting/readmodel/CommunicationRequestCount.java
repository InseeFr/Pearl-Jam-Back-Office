package fr.insee.pearljam.domain.reporting.readmodel;

public record CommunicationRequestCount(
        String entityId,
        Long noticeCount,
        Long reminderCount) {
    
    public static CommunicationRequestCount empty(String id) {
        return new CommunicationRequestCount(id, 0L, 0L);
    }
}