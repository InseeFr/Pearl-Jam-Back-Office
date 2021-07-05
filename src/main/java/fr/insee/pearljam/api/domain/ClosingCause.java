package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import fr.insee.pearljam.api.dto.closingcause.ClosingCauseDto;

/**
* Entity ClosingCause : represent the entity table in DB
* 
* 
*/
@Entity
@Table
public class ClosingCause implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1306796886154551063L;
	/**
	 * the id of ContactOutcome
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * the date of ContactOutcome
	 */
	@Column
	private Long date;
	/**
	 * the OutcomeType of ContactOutcome
	 */
	@Enumerated(EnumType.STRING)
	@Column
	private ClosingCauseType type;
	
	/**
	* The SurveyUnit associated to ContactOutcome 
	*/
	@OneToOne
	private SurveyUnit surveyUnit;

		public ClosingCause() {
		super();
	}
		
	public ClosingCause(Long id, Long date, ClosingCauseType type, SurveyUnit surveyUnit) {
		super();
		this.id = id;
		this.date = date;
		this.type = type;
		this.surveyUnit = surveyUnit;
	}
	
	public ClosingCause(ClosingCauseDto closingCause, SurveyUnit surveyUnit) {
		this.date = closingCause.getDate();
		this.type = closingCause.getType();
		this.surveyUnit = surveyUnit;
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
	 * @return the date
	 */
	public Long getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Long date) {
		this.date = date;
	}
	/**
	 * @return the outcomeType
	 */
	public ClosingCauseType getType() {
		return type;
	}
	/**
	 * @param contactOutcomeType the outcomeType to set
	 */
	public void setType(ClosingCauseType type) {
		this.type = type;
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
