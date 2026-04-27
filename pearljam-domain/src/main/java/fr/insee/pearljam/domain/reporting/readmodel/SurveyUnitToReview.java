package fr.insee.pearljam.domain.reporting.readmodel;

/**
 * Read model representing a survey unit that needs to be reviewed.
 * This immutable record contains the essential information for displaying survey units
 * that are in a "To Be Reviewed" (TBR) state.
 *
 * @param id the unique identifier of the survey unit
 * @param campaignLabel the label of the campaign
 * @param contactOutcome the contact outcome of the survey unit
 * @param interviewerId the identifier of the interviewer
 * @param interviewerName the name of the interviewer
 * @param viewed whether the survey unit has been viewed
 * @param lastComment the last comment on the survey unit
 */
public record SurveyUnitToReview(
        String id,
        String campaignLabel,
        String contactOutcome,
        String interviewerId,
        String interviewerName,
        Boolean viewed,
        String lastComment
) {
}
