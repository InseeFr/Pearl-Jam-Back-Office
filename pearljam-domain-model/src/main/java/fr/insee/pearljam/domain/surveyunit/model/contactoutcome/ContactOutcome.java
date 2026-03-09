package fr.insee.pearljam.domain.surveyunit.model.contactoutcome;

public record ContactOutcome(
		Long id,
		Long date,
		ContactOutcomeType type,
		Integer totalNumberOfContactAttempts,
		String surveyUnitId
) {
}
