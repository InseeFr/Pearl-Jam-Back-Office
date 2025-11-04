package fr.insee.pearljam.surveyunit.domain.model;

import fr.insee.pearljam.campaign.domain.model.ContactOutcomeType;

public record ContactOutcome(
		Long id,
		Long date,
		ContactOutcomeType type,
		Integer totalNumberOfContactAttempts,
		String surveyUnitId
) {
}
