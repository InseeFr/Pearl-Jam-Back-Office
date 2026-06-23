package fr.insee.pearljam.domain.campaign.model.communication;

public enum CommunicationType {
    REMINDER, NOTICE;

    public static CommunicationType fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("CommunicationType code cannot be null");
        }

        return switch (code.toUpperCase()) {
            case "REMINDER" -> REMINDER;
            case "NOTICE" -> NOTICE;
            default -> throw new IllegalArgumentException(
                    "Unknown CommunicationType: " + code
            );
        };
    }
}
