package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;

/**
 * Entity ContactOutcome : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
@Entity
@Table
public class ContactOutcome implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1056818927018376638L;
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
	private ContactOutcomeType type;
	/**
	 * the totalNumberOfContactAttempts of ContactOutcome
	 */
	@Column
	private Integer totalNumberOfContactAttempts;
	/**
	 * The SurveyUnit associated to ContactOutcome
	 */
	@OneToOne
	private SurveyUnit surveyUnit;

	public ContactOutcome(Long id, Long date, ContactOutcomeType type, Integer totalNumberOfContactAttempts,
			SurveyUnit surveyUnit) {
		super();
		this.id = id;
		this.date = date;
		this.type = type;
		this.totalNumberOfContactAttempts = totalNumberOfContactAttempts;
		this.surveyUnit = surveyUnit;
	}

	public ContactOutcome() {
		super();
	}

	public ContactOutcome(ContactOutcomeDto contactOutcome, SurveyUnit surveyUnit) {
		this.date = contactOutcome.getDate();
		this.type = contactOutcome.getType();
		this.totalNumberOfContactAttempts = contactOutcome.getTotalNumberOfContactAttempts();
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
	public ContactOutcomeType getType() {
		return type;
	}

	/**
	 * @param contactOutcomeType the outcomeType to set
	 */
	public void setType(ContactOutcomeType type) {
		this.type = type;
	}

	/**
	 * @return the totalNumberOfContactAttempts
	 */
	public Integer getTotalNumberOfContactAttempts() {
		return totalNumberOfContactAttempts;
	}

	/**
	 * @param totalNumberOfContactAttempts the totalNumberOfContactAttempts to set
	 */
	public void setTotalNumberOfContactAttempts(Integer totalNumberOfContactAttempts) {
		this.totalNumberOfContactAttempts = totalNumberOfContactAttempts;
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
