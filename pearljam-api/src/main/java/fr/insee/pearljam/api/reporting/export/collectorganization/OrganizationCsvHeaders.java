package fr.insee.pearljam.api.reporting.export.collectorganization;

import lombok.Getter;

public enum OrganizationCsvHeaders {
    INTERVIEWER_LABEL("Nom Prénom Enquêteur"),
    INTERVIEWER_ID("Idep Enquêteur"),
    SURVEY_UNITS_COUNT("Nombre d'UE");

    @Getter
    private final String headerName;

    OrganizationCsvHeaders(String headerName) {
        this.headerName = headerName;
    }
}
