package fr.insee.pearljam.domain.campaign.model;

import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;

/**
 * Domain model representing a survey campaign.
 *
 * @param id                             The campaign identifier
 * @param label                          The campaign label
 * @param identificationConfiguration   The identification configuration
 * @param contactOutcomeConfiguration   The contact outcome configuration
 * @param contactAttemptConfiguration   The contact attempt configuration
 * @param email                          The email associated with the campaign
 * @param sensitivity                    Whether the campaign data is sensitive
 * @param collectNextContacts            Whether to collect future contacts data
 */
public record Campaign(
        String id,
        String label,
        IdentificationConfiguration identificationConfiguration,
        ContactOutcomeConfiguration contactOutcomeConfiguration,
        ContactAttemptConfiguration contactAttemptConfiguration,
        String email,
        Boolean sensitivity,
        boolean collectNextContacts
) {
}
