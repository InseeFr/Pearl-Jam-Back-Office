package fr.insee.pearljam.api.domain;

import java.io.Serializable;

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
import fr.insee.pearljam.api.dto.state.StateDto;

/**
 * Entity State : represent the entity table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class State implements Serializable {
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
	@Column(length = 3)
	private StateType type;

	/**
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

	public State(StateDto s, SurveyUnit surveyUnit) {
		super();
		this.date = s.getDate();
		this.surveyUnit = surveyUnit;
		this.type = s.getType();
	}

}
