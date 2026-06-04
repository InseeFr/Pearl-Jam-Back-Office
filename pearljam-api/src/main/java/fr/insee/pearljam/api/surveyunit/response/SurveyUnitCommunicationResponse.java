package fr.insee.pearljam.api.surveyunit.response;

public record SurveyUnitCommunicationResponse(
        String type, Long date, String reason
) {
}
