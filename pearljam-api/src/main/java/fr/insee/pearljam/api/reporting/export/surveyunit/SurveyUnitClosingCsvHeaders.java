package fr.insee.pearljam.api.reporting.export.surveyunit;

import lombok.Getter;

public enum SurveyUnitClosingCsvHeaders {
    CAMPAIGN("Enquête")
    INTERVIEWER_LABEL("Nom Prénom Enquêteur"),
    INTERVIEWER_ID("Idep Enquêteur"),
    SURVEY_UNITS_COUNT("Nombre d'UE");

    @Getter
    private final String headerName;

    SurveyUnitClosingCsvHeaders(String headerName) {
        this.headerName = headerName;
    }
}


//Enquête
//Identifiant
//Identifiant de l'ue
//Nom Prénom Enquêteur
//Idep Enquêteur
//Ssech
//        Département
//Commune (libellé)
//Etat du repérage (libellé)
//Bilan des contacts (libellé)
//Etat du questionnaire	(libellé)
//Motif provisoire de cloture (libellé)