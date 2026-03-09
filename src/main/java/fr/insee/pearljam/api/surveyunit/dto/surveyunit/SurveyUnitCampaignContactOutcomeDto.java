package fr.insee.pearljam.api.surveyunit.dto.surveyunit;

import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;

/**
 * Record representing a ContactOutcomeDto
 *
 * @param type                         The type of the contactOutcome.
 */
public record SurveyUnitCampaignContactOutcomeDto(ContactOutcomeType type) {
}
