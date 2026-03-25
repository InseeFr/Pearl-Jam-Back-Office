package fr.insee.pearljam.domain.reporting.query;

public record CommunicationRequestCountQueryResponse(
        String entityId,
        Long noticeCount,
        Long reminderCount) {
    
    public static CommunicationRequestCountQueryResponse empty(String id) {
        return new CommunicationRequestCountQueryResponse(id, 0L, 0L);
    }
}