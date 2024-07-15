package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentDto(
		@NotNull
		CommentType type,
		@NotNull
		@Size(max = 999)
		String value) {

	public static Comment toModel(String surveyUnitId, CommentDto commentDto) {
		return new Comment(commentDto.type(), commentDto.value(), surveyUnitId);
	}

	public static CommentDto fromModel(Comment comment) {
		return new CommentDto(comment.type(), comment.value());
	}
}
