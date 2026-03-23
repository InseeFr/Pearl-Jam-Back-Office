package fr.insee.pearljam.domain.reporting.projection;

public record CommunicationRequestCountProjection(
        String entityId,
        Long noticeCount,
        Long reminderCount) {
    
    public static CommunicationRequestCountProjection empty(String id) {
        return new CommunicationRequestCountProjection(id, 0L, 0L);
    }
}