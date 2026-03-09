package fr.insee.pearljam.api.surveyunit.dto.state;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.StateDB;
import fr.insee.pearljam.domain.surveyunit.model.StateType;

public record StateDto(
		Long id,
		Long date,
		StateType type) {

	public StateDto(StateDB state) {
		this(state.getId(), state.getDate(), state.getType());
	}
}
