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

import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;

/**
* Entity ContactAttempt : represent the entity table in DB
* 
* @author Claudel Benjamin, SimonDmz
* 
*/
@Entity
@Table
public class ContactAttempt implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2015739722235846385L;
	/**
	 * the id of ContactAttempt
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * the date of ContactAttempt
	 */
	@Column
	private Long date;
	/**
	 * the Status of ContactAttempt
	 */
	@Enumerated(EnumType.STRING)
	@Column
	private Status status;

	@Column
	private String medium;

	/**
	* The SurveyUnit associated to Contact attempt 
	*/
	@ManyToOne
	private SurveyUnit surveyUnit;

	public ContactAttempt() {

	}

	public ContactAttempt(ContactAttemptDto dto, SurveyUnit surveyUnit) {
		super();
		this.date = dto.getDate();
		this.status = dto.getStatus();
		this.surveyUnit = surveyUnit;
		this.medium = dto.getMedium();
	}
	
	/**
	 * @param id
	 * @param date
	 * @param status
	 * @param surveyUnit
	 */
	public ContactAttempt(Long date, Status status, String medium, SurveyUnit surveyUnit) {
		super();
		this.date = date;
		this.status = status;
		this.medium = medium;
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
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
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

	public String getMedium() {
		return this.medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}
}
