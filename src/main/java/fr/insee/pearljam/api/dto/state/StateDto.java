package fr.insee.pearljam.api.dto.state;

import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.StateType;

public record StateDto(
		Long id,
		Long date,
		StateType type) {

	public StateDto(State state) {
		this(state.getId(), state.getDate(), state.getType());
	}

	public Long getId() {
		return id;
	}

	public Long getDate() {
		return date;
	}

	public StateType getType() {
		return type;
	}

}
