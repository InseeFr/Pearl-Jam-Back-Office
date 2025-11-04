package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.state;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.State;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.StateType;

public record StateDto(
		Long id,
		Long date,
		StateType type) {

	public StateDto(State state) {
		this(state.getId(), state.getDate(), state.getType());
	}
}
