package fr.insee.pearljam.infrastructure.surveyunit.entity;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.Comment;
import fr.insee.pearljam.domain.surveyunit.model.CommentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity Comment : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
@Entity(name = "comment")
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDB implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 6363481673399032153L;
	/**
	 * The id of Address
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * The type of Comment
	 */
	@Enumerated(EnumType.STRING)
	@Column(length = 11)
	private CommentType type;

	/**
	 * The value of Comment
	 */
	@Column(length = 999)
	private String value;

	/**
	 * The SurveyUnit associated to Comment
	 */
	@ManyToOne
	private SurveyUnit surveyUnit;

	/**
	 * Create entity object from model object
	 * @param surveyUnit survey unit entity
	 * @param comment comment model object
	 * @return comment entity object
	 */
	public static CommentDB fromModel(SurveyUnit surveyUnit, Comment comment) {
		return new CommentDB(null, comment.type(), comment.value(), surveyUnit);
	}

	/**
	 * Create model object from entity
	 * @param comment entity object
	 * @return comment model object
	 */
	public static Comment toModel(CommentDB comment) {
		return new Comment(comment.getType(), comment.getValue(), comment.getSurveyUnit().getId());
	}

	/**
	 * Create model objects from entities
	 * @param comments entities object
	 * @return comment models object
	 */
	public static Set<Comment> toModel(Set<CommentDB> comments) {
		return comments.stream()
				.map(CommentDB::toModel)
				.collect(Collectors.toSet());
	}
}
