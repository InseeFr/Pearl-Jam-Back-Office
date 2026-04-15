package fr.insee.pearljam.api.reporting.export.progress;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ProgressCsvHeaders {
    // specific headers
    CAMPAIGN_LABEL("Enquête"),
    INTERVIEWER_LABEL("Enquêteur"),
    ORGANIZATION_UNIT_LABEL("Site"),

    // common headers
    PROGRESS_RATE("Taux d'avancement"),
    ALLOCATED("Confiées"),
    NOT_STARTED("Non commencées"),
    IN_PROGRESS("En cours enquêteur"),
    PENDING_TRANSMISSION("En attente de transmission enquêteur"),
    TO_REVIEW("À lire"),
    VALIDATED("Validées terminées"),
    PREPARING_CONTACT("En préparation"),
    WITH_CONTACT("Au moins un repérage ou un contact"),
    WITH_APPOINTMENT("RDV pris"),
    STARTED("Questionnaire démarré"),
    NOTICE_LETTER("Lettre avis"),
    REMINDER_LETTER("Relances");

    @Getter
    private final String headerName;

    ProgressCsvHeaders(String headerName) {
        this.headerName = headerName;
    }

    public static List<ProgressCsvHeaders> commonHeaders() {
        return List.of(
                PROGRESS_RATE, ALLOCATED, NOT_STARTED, IN_PROGRESS,
                PENDING_TRANSMISSION, TO_REVIEW, VALIDATED,
                PREPARING_CONTACT, WITH_CONTACT, WITH_APPOINTMENT,
                STARTED, NOTICE_LETTER, REMINDER_LETTER
        );
    }

    public static List<ProgressCsvHeaders> buildHeaders(List<ProgressCsvHeaders> prefixHeaders) {
        List<ProgressCsvHeaders> headers = new ArrayList<>(prefixHeaders);
        headers.addAll(commonHeaders());
        return Collections.unmodifiableList(headers);
    }
}
