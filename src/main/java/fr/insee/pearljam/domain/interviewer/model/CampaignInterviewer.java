package fr.insee.pearljam.domain.interviewer.model;

/**
 * Lightweight projection representing an interviewer attached to a campaign
 * with contact details and assigned survey-unit count.
 */
public record CampaignInterviewer(
		String id,
		String firstName,
		String lastName,
		String email,
		String phoneNumber,
		Long surveyUnitCount) {
}
