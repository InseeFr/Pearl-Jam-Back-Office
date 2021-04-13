package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
* Entity State : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table
public class State implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4610792644448862048L;

	/**
	* The id of State 
	*/
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	* The save date of State 
	*/
	@Column
	private Long date;
	
	@ManyToOne
	private SurveyUnit surveyUnit;
	
	/**
	* The type of State 
	*/
	@Enumerated(EnumType.STRING)
	@Column(length=3)
	private StateType type;

	public State() {
		
	}	
	
	/**
	 * @param id
	 * @param date
	 * @param surveyUnit
	 * @param type
	 */
	public State(Long date, SurveyUnit surveyUnit, StateType type) {
		super();
		this.date = date;
		this.surveyUnit = surveyUnit;
		this.type = type;
	}
	/**
	 * @return id of comment
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return date of State
	 */
	public Long getDate() {
		return date;
	}
	/**
	 * @param date date to set
	 */
	public void setDate(Long date) {
		this.date = date;
	}
	/**
	 * @return type of State
	 */
	public StateType getType() {
		return type;
	}
	/**
	 * @param type type to set
	 */
	public void setType(StateType type) {
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
