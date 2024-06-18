package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import fr.insee.pearljam.api.dto.comment.CommentDto;
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
@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {
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

	public Comment(CommentDto dto, SurveyUnit surveyUnit) {
		super();
		this.type = dto.getType();
		String commentValue = dto.getValue();
		this.value = commentValue.length() > 999 ? commentValue.substring(0, 999) : commentValue;
		this.surveyUnit = surveyUnit;
	}

}
