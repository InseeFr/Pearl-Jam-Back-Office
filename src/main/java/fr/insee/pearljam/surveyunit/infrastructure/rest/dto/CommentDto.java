package fr.insee.pearljam.surveyunit.infrastructure.rest.dto;

import fr.insee.pearljam.surveyunit.domain.model.Comment;
import fr.insee.pearljam.surveyunit.domain.model.CommentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

/**
 * Record representing a CommentDto
 *
 * @param type  The type of the comment.
 * @param value The value of the comment.
 */
public record CommentDto(
		@NotNull
		CommentType type,
		@NotNull
		@Size(max = 999)
		String value) {

	/**
	 * Converts a CommentDto to a Comment model.
	 *
	 * @param surveyUnitId The ID of the survey unit associated with the comment.
	 * @param commentDto   The CommentDto to convert.
	 * @return A new Comment model instance.
	 */
	public static Comment toModel(String surveyUnitId, CommentDto commentDto) {
		return new Comment(commentDto.type(), commentDto.value(), surveyUnitId);
	}

	/**
	 * Converts a Comment model to a CommentDto.
	 *
	 * @param comment The Comment model to convert.
	 * @return A new CommentDto instance.
	 */
	public static CommentDto fromModel(Comment comment) {
		return new CommentDto(comment.type(), comment.value());
	}

	/**
	 * Converts a set of Comment models to a list of CommentDto.
	 *
	 * @param comments The set of Comment models to convert.
	 * @return A list of CommentDto instances.
	 */
	public static List<CommentDto> fromModel(Set<Comment> comments) {
		return comments.stream()
				.map(CommentDto::fromModel)
				.toList();
	}
}