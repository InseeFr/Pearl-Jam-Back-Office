package fr.insee.pearljam.domain.surveyunit.readmodel;


public record SurveyUnitAssigned(
        String surveyUnitId,
        String surveyUnitDisplayName,
        String ssech,
        String interviewerFirstName,
        String interviewerLastName,
        String addressL6,
        String questionnaireState,
        String closingCause
) {
}
