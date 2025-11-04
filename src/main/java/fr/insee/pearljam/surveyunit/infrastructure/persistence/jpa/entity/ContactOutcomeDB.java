package fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity;


import fr.insee.pearljam.campaign.domain.model.ContactOutcomeType;
import fr.insee.pearljam.surveyunit.domain.model.ContactOutcome;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "contact_outcome")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContactOutcomeDB implements Serializable {

	@Serial
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

	/**
	 * Create entity object from model object
	 * @param surveyUnit survey unit entity
	 * @param contactOutcome contact-outcome model object
	 * @return contactOutcome entity object
	 */
	public static ContactOutcomeDB fromModel(SurveyUnit surveyUnit, ContactOutcome contactOutcome) {
		return new ContactOutcomeDB(null, contactOutcome.date(),contactOutcome.type(),contactOutcome.totalNumberOfContactAttempts(), surveyUnit);
	}

	/**
	 * Create model object from entity
	 * @param contactOutcome entity object
	 * @return contactOutcome model object
	 */
	public static ContactOutcome toModel(ContactOutcomeDB contactOutcome) {
		return new ContactOutcome(contactOutcome.getId(),contactOutcome.getDate(), contactOutcome.getType(),contactOutcome.getTotalNumberOfContactAttempts(),contactOutcome.getSurveyUnit().getId());
	}

	public void updateContactOutcome(ContactOutcome contactOutcome) {
		setDate(contactOutcome.date());
		setType(contactOutcome.type());
		setTotalNumberOfContactAttempts(contactOutcome.totalNumberOfContactAttempts());
	}
}
