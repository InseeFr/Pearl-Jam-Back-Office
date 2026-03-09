package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.domain.contactoutcome.model.ContactOutcomeType;

/**
 * Record representing a ContactOutcomeDto
 *
 * @param type                         The type of the contactOutcome.
 */
public record SurveyUnitCampaignContactOutcomeDto(ContactOutcomeType type) {
}
