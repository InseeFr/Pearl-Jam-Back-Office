package fr.insee.pearljam.api.surveyunit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.domain.ContactOutcomeType;

/**
 * Record representing a ContactOutcomeDto
 *
 * @param type                         The type of the contactOutcome.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClosableContactOutcomeDto(ContactOutcomeType type) {
}

