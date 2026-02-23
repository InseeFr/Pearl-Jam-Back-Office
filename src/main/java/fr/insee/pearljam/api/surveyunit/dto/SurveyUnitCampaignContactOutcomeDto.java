package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.api.domain.ContactOutcomeType;

/**
 * Record representing a ContactOutcomeDto
 *
 * @param type                         The type of the contactOutcome.
 */
public record SurveyUnitCampaignContactOutcomeDto(ContactOutcomeType type) {
}
