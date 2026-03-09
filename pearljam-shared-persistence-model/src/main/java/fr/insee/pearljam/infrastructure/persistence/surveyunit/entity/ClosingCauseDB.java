package fr.insee.pearljam.infrastructure.persistence.surveyunit.entity;

import java.io.Serial;
import java.io.Serializable;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
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
import fr.insee.pearljam.contracts.surveyunit.dto.closingcause.ClosingCauseDto;

/**
 * Entity ClosingCause : represent the entity table in DB
 * 
 * 
 */
@Entity
@Table(name = "closing_cause", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClosingCauseDB implements Serializable {
	/**
	 * 
	 */
	@Serial
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
	private SurveyUnitDB surveyUnit;

	public ClosingCauseDB(ClosingCauseDto closingCause, SurveyUnitDB surveyUnit) {
		this.date = closingCause.getDate();
		this.type = closingCause.getType();
		this.surveyUnit = surveyUnit;
	}

}
