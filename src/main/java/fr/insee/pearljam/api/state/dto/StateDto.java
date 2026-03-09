package fr.insee.pearljam.api.state.dto;

import fr.insee.pearljam.domain.state.model.State;
import fr.insee.pearljam.domain.state.model.StateType;

public record StateDto(
		Long id,
		Long date,
		StateType type) {

	public StateDto(State state) {
		this(state.getId(), state.getDate(), state.getType());
	}
}
