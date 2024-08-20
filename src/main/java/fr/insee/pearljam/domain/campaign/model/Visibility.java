package fr.insee.pearljam.domain.campaign.model;

import fr.insee.pearljam.domain.exception.VisibilityHasInvalidDatesException;

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
public record Visibility(
        String campaignId,
        String organizationalUnitId,
        Long managementStartDate,
        Long interviewerStartDate,
        Long identificationPhaseStartDate,
        Long collectionStartDate,
        Long collectionEndDate,
        Long endDate
) {
    public static Visibility merge(Visibility currentVisibility, Visibility visibilityToUpdate) throws VisibilityHasInvalidDatesException {
        Long managementStartDate = visibilityToUpdate.managementStartDate() != null ?
                visibilityToUpdate.managementStartDate() : currentVisibility.managementStartDate();
        Long interviewerStartDate = visibilityToUpdate.interviewerStartDate() != null ?
                visibilityToUpdate.interviewerStartDate() : currentVisibility.interviewerStartDate();
        Long identificationPhaseStartDate = visibilityToUpdate.identificationPhaseStartDate() != null ?
                visibilityToUpdate.identificationPhaseStartDate() : currentVisibility.identificationPhaseStartDate();
        Long collectionStartDate = visibilityToUpdate.collectionStartDate() != null ?
                visibilityToUpdate.collectionStartDate() : currentVisibility.collectionStartDate();
        Long collectionEndDate = visibilityToUpdate.collectionEndDate() != null ?
                visibilityToUpdate.collectionEndDate() : currentVisibility.collectionEndDate();
        Long endDate = visibilityToUpdate.endDate() != null ?
                visibilityToUpdate.endDate() : currentVisibility.endDate();

        Visibility updatedVisibility = new Visibility(
                currentVisibility.campaignId(),
                currentVisibility.organizationalUnitId(),
                managementStartDate,
                interviewerStartDate,
                identificationPhaseStartDate,
                collectionStartDate,
                collectionEndDate,
                endDate);

        if(isValid(updatedVisibility)) {
            return updatedVisibility;
        }

        throw new VisibilityHasInvalidDatesException();
    }

    public static boolean isValid(Visibility visibility) {
        return visibility.managementStartDate() < visibility.interviewerStartDate()
                && visibility.interviewerStartDate() < visibility.identificationPhaseStartDate()
                && visibility.identificationPhaseStartDate() < visibility.collectionStartDate()
                && visibility.collectionStartDate() < visibility.collectionEndDate()
                && visibility.collectionEndDate() < visibility.endDate();
    }
}