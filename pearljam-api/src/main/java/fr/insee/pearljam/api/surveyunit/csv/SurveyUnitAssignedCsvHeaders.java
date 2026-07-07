package fr.insee.pearljam.api.surveyunit.csv;

public enum SurveyUnitAssignedCsvHeaders {

    TECHNICAL_SURVEY_UNIT_ID("Identifiant technique"),
    SURVEY_UNIT_ID("Identifiant de l'ue"),
    SUB_SAMPLE_ID("Ssech"),
    INTERVIEWER_ID( "Idep"),
    LOCATION("Département"),
    CITY("Commune"),
    SURVEY_UNIT_STATE("Etat de l'UE"),
    CLOSING_CAUSE("Motif provisoire");

    private final String headerName;

    SurveyUnitAssignedCsvHeaders(String headerName) {
        this.headerName = headerName;
    }

    public String headerName() {
        return headerName;
    }

}
