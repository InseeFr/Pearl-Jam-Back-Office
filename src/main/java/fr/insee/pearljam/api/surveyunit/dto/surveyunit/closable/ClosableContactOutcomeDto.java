package fr.insee.pearljam.api.surveyunit.dto.surveyunit.closable;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;

/**
 * Record representing a ContactOutcomeDto
 *
 * @param type                         The type of the contactOutcome.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClosableContactOutcomeDto(ContactOutcomeType type) {
}
