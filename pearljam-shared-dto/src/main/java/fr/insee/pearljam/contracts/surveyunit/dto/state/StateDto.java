package fr.insee.pearljam.contracts.surveyunit.dto.state;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import jakarta.validation.constraints.NotNull;

public record StateDto(
		Long id,
		@NotNull
		Long date,
		StateType type) {}
