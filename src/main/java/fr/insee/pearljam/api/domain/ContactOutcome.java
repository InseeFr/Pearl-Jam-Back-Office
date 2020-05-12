package fr.insee.pearljam.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
* Entity ContactOutcome : represent the entity table in DB
* 
* @author Claudel Benjamin
* 
*/
@Entity
@Table
@SequenceGenerator(
name = "seqid-gen", 
sequenceName = "CONTACT_OUTCOME_SEQ" ,
initialValue = 10, allocationSize = 1)
public class ContactOutcome {
	/**
	 * the id of ContactOutcome
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqid-gen")
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
	@ManyToOne
	private SurveyUnit surveyUnit;
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
