package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import fr.insee.pearljam.api.dto.contactoutcome.ContactOutcomeDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity ContactOutcome : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

	public ContactOutcome(ContactOutcomeDto contactOutcome, SurveyUnit surveyUnit) {
		this.date = contactOutcome.getDate();
		this.type = contactOutcome.getType();
		this.totalNumberOfContactAttempts = contactOutcome.getTotalNumberOfContactAttempts();
		this.surveyUnit = surveyUnit;
	}

}
