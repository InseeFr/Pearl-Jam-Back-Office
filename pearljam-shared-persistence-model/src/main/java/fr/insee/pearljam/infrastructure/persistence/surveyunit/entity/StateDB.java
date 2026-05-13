package fr.insee.pearljam.infrastructure.persistence.surveyunit.entity;

import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serial;
import java.io.Serializable;

/**
 * Entity State : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
@Entity
@Table(name = "state", schema = "public")
@NoArgsConstructor
@Getter
@Setter
public class StateDB implements Serializable {

	@Serial
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
	private SurveyUnitDB surveyUnit;

	/**
	 * The type of State
	 */
	@Enumerated(EnumType.STRING)
	@Column(length = 3)
	private StateType type;

	/**
	 * @param date
	 * @param surveyUnit
	 * @param type
	 */
	public StateDB(Long date, SurveyUnitDB surveyUnit, StateType type) {
		super();
		this.date = date;
		this.surveyUnit = surveyUnit;
		this.type = type;
	}

	public StateDB(StateDto s, SurveyUnitDB surveyUnit) {
		super();
		this.date = s.date();
		this.surveyUnit = surveyUnit;
		this.type = s.type();
	}
}
