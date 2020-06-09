package fr.insee.pearljam.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
/**
* Entity Comment : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table
public class Comment {
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
	@Column(length=11)
	private CommentType type;
	
	/**
	* The value of Comment 
	*/
	@Column(length=255)
	private String value;
	
	/**
	* The SurveyUnit associated to Comment 
	*/
	@ManyToOne
	private SurveyUnit surveyUnit;
	
	public Comment(Long id, CommentType type, String value, @Size(min = 0, max = 2) SurveyUnit surveyUnit) {
		super();
		this.id = id;
		this.type = type;
		this.value = value;
		this.surveyUnit = surveyUnit;
	}
	public Comment() {
		super();
	}
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the type
	 */
	public CommentType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(CommentType type) {
		this.type = type;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the surveyUnit
	 */
	public SurveyUnit getSurveyUnit() {
		return surveyUnit;
	}
	/**
	 * @param surveyUnit the surveyUnit to set
	 */
	public void setSurveyUnit(SurveyUnit surveyUnit) {
		this.surveyUnit = surveyUnit;
	}
	
}
