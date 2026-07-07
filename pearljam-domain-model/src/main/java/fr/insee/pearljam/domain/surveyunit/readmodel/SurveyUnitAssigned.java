package fr.insee.pearljam.domain.surveyunit.readmodel;


public record SurveyUnitAssigned(
        String surveyUnitId,
        String surveyUnitDisplayName,
        String ssech,
        String interviewerId,
        String interviewerFirstName,
        String interviewerLastName,
        String location,
        String city,
        String questionnaireState,
        String closingCause
) {
}
