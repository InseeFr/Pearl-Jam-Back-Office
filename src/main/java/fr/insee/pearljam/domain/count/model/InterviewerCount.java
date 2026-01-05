package fr.insee.pearljam.domain.count.model;

public record InterviewerCount(
        String id,
        String firstName,
        String lastName,
        Long surveyUnitCount
) {
}
