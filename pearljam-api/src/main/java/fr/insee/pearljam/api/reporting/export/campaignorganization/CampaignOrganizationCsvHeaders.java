package fr.insee.pearljam.api.reporting.export.campaignorganization;

import lombok.Getter;

public enum CampaignOrganizationCsvHeaders {
    INTERVIEWER_LABEL("Nom Prénom Enquêteur"),
    INTERVIEWER_ID("Idep Enquêteur"),
    SURVEY_UNITS_COUNT("Nombre d'UE");

    @Getter
    private final String headerName;

    CampaignOrganizationCsvHeaders(String headerName) {
        this.headerName = headerName;
    }
}
