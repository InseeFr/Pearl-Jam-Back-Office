package fr.insee.pearljam.api.domain;

import java.io.Serial;
import java.io.Serializable;

import fr.insee.pearljam.api.dto.contactattempt.ContactAttemptDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity ContactAttempt : represent the entity table in DB
 * 
 * @author Claudel Benjamin, SimonDmz
 * 
 */
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class ContactAttempt implements Serializable {
	/**
	 * 
	 */
	@Serial
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

	@Enumerated(EnumType.STRING)
	@Column
	private Medium medium;

	/**
	 * The SurveyUnit associated to Contact attempt
	 */
	@ManyToOne
	private SurveyUnit surveyUnit;

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
	public ContactAttempt(Long date, Status status, Medium medium, SurveyUnit surveyUnit) {
		super();
		this.date = date;
		this.status = status;
		this.medium = medium;
		this.surveyUnit = surveyUnit;
	}

}
