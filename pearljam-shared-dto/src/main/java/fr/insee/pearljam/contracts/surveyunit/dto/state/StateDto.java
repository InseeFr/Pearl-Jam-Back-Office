package fr.insee.pearljam.contracts.surveyunit.dto.state;

import fr.insee.pearljam.domain.surveyunit.model.StateType;

public record StateDto(
		Long id,
		Long date,
		StateType type) {}
