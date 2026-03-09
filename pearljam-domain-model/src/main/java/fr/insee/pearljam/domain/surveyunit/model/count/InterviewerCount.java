package fr.insee.pearljam.domain.surveyunit.model.count;

public record InterviewerCount(
        String id,
        String firstName,
        String lastName,
        Long surveyUnitCount
) {
}
