package fr.insee.pearljam.api.surveyunit.controller.export.closing;

import lombok.Getter;
import java.util.List;

public enum SurveyUnitClosingCsvHeaders {
    CAMPAIGN("Enquête"),
    ID("Identifiant"),
    SURVEYUNIT_ID("Identifiant de l'ue"),
    INTERVIEWER_LABEL("Nom Prénom Enquêteur"),
    INTERVIEWER_ID("Idep Enquêteur"),
    SSECH("Ssech"),
    DEPARTEMENT("Département"),
    CITY("Commune"),
    IDENTIFICATION("Etat du repérage"),
    CONTACT_OUTCOME("Bilan des contacts"),
    QUESTIONNAIRE_STATE("Etat du questionnaire"),
    PROVISIONAL_CLOSING_CAUSE("Motif provisoire de cloture");

    @Getter
    private final String headerName;

    SurveyUnitClosingCsvHeaders(String headerName) {
        this.headerName = headerName;
    }

    public static List<SurveyUnitClosingCsvHeaders> commonHeaders() {
        return List.of(
                CAMPAIGN,
                ID,
                SURVEYUNIT_ID,
                INTERVIEWER_LABEL,
                INTERVIEWER_ID,
                SSECH,
                DEPARTEMENT,
                CITY,
                IDENTIFICATION,
                CONTACT_OUTCOME,
                QUESTIONNAIRE_STATE,
                PROVISIONAL_CLOSING_CAUSE
        );
    }

    public static List<SurveyUnitClosingCsvHeaders> buildHeaders() {
        return commonHeaders();
    }
}