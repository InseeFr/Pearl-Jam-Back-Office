package fr.insee.pearljam.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
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
	@GeneratedValue
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
	@Column
	private String value;
	
	/**
	* The SurveyUnit associated to Comment 
	*/
	@ManyToOne
	@Size(min=0, max=2)
	private SurveyUnit SurveyUnit;
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
		return SurveyUnit;
	}
	/**
	 * @param surveyUnit the surveyUnit to set
	 */
	public void setSurveyUnit(SurveyUnit surveyUnit) {
		SurveyUnit = surveyUnit;
	}
	
}
