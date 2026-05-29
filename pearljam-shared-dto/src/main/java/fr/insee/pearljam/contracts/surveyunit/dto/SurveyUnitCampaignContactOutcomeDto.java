package fr.insee.pearljam.contracts.surveyunit.dto;

import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;

/**
 * Record representing a ContactOutcomeDto
 *
 * @param type                         The type of the contactOutcome.
 */
public record SurveyUnitCampaignContactOutcomeDto(ContactOutcomeType type) {
}
