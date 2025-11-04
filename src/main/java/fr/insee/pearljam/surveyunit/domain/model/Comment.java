package fr.insee.pearljam.surveyunit.domain.model;

public record Comment(
        CommentType type,
        String value,
        String surveyUnitId) {
}
