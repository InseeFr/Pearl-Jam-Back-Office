package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.api.domain.ContactOutcomeType;

public record ContactOutcome(
		Long id,
		Long date,
		ContactOutcomeType type,
		Integer totalNumberOfContactAttempts,
		String surveyUnitId
) {
}
