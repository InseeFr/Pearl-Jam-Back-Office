package fr.insee.pearljam.domain.campaign.model;

/**
 * Domain model representing a campaign referent (contact person).
 *
 * @param firstName   The referent's first name
 * @param lastName    The referent's last name
 * @param phoneNumber The referent's phone number
 * @param role        The referent's role
 */
public record Referent(
        String firstName,
        String lastName,
        String phoneNumber,
        String role
) {
}
