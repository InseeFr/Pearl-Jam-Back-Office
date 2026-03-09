package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.domain.contactoutcome.model.ContactOutcomeType;

public record ContactOutcome(
		Long id,
		Long date,
		ContactOutcomeType type,
		Integer totalNumberOfContactAttempts,
		String surveyUnitId
) {
}
