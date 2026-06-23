package fr.insee.pearljam.domain.surveyunit.model.communication;

public enum CommunicationRequestReason {
    REFUSAL, UNREACHABLE;


    public static CommunicationRequestReason fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("CommunicationRequestReason code cannot be null");
        }

        return switch (code.toUpperCase()) {
            case "REFUSAL" -> REFUSAL;
            case "UNREACHABLE" -> UNREACHABLE;
            default -> throw new IllegalArgumentException(
                    "Unknown CommunicationRequestReason: " + code
            );
        };
    }
}
