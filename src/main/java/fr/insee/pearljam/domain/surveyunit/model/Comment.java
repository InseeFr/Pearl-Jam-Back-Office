package fr.insee.pearljam.domain.surveyunit.model;

public record Comment(
        CommentType type,
        String value,
        String surveyUnitId) {
}
