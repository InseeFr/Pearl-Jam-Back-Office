package fr.insee.pearljam.api.surveyunit.controller.export.completed;

import lombok.Getter;

import java.util.List;

public enum SurveyUnitCompletedCsvHeaders {

    ID("Identifiant"),
    SURVEYUNIT_ID("Identifiant de l'ue"),
    INTERVIEWER_LABEL("Nom Prénom Enquêteur"),
    INTERVIEWER_ID("Idep Enquêteur"),
    END_DATE("Date de finalisation"),
    CONTACT_OUTCOME("Bilan de contact"),
    CLOSING_CAUSE("Motif de cloture"),
    VIEWED("Lecture"),
    COMMENT("Commentaire");
    @Getter
    private final String headerName;

    SurveyUnitCompletedCsvHeaders(String headerName) {
        this.headerName = headerName;
    }

    public static List<SurveyUnitCompletedCsvHeaders> commonHeaders() {
        return List.of(
                ID,
                SURVEYUNIT_ID,
                INTERVIEWER_LABEL,
                INTERVIEWER_ID,
                END_DATE,
                CONTACT_OUTCOME,
                CLOSING_CAUSE,
                VIEWED,
                COMMENT
        );
    }

    public static List<SurveyUnitCompletedCsvHeaders> buildHeaders() {
        return commonHeaders();
    }
}