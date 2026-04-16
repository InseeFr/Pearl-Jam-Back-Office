package fr.insee.pearljam.domain.campaign.readmodel;

/**
 * A class representing the visibility of a campaign for an organizational unit
 *
 * @param managementStartDate          The start date of Visibility of management
 * @param interviewerStartDate         The start date of Visibility of interviewer
 * @param identificationPhaseStartDate The start date of identification phase
 * @param collectionStartDate          The start date of collection
 * @param collectionEndDate            The end date of collection
 * @param endDate                      The end date of visibility
 */

public record CampaignVisibility(
        String id,
        String label,
        String email,
        Long managementStartDate,
        Long interviewerStartDate,
        Long identificationPhaseStartDate,
        Long collectionStartDate,
        Long collectionEndDate,
        Long endDate) {
}
