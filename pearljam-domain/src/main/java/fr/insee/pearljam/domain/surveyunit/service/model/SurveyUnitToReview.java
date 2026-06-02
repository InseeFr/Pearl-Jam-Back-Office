package fr.insee.pearljam.domain.surveyunit.service.model;

/**
 * Read model representing a survey unit that needs to be reviewed.
 * This immutable record contains the essential information for displaying survey units
 * that are in a "To Be Reviewed" (TBR) state.
 *
 * @param id the unique identifier of the survey unit
 * @param surveyUnitDisplayName display name of the survey unit
 * @param campaignLabel the label of the campaign
 * @param contactOutcome the contact outcome of the survey unit
 * @param interviewerId the identifier of the interviewer
 * @param interviewerFirstName the first name of the interviewer
 * @param interviewerLastName the last name of the interviewer
 * @param viewed whether the survey unit has been viewed
 * @param lastComment the last comment on the survey unit
 */
public record SurveyUnitToReview(
        String id,
        String surveyUnitDisplayName,
        String campaignLabel,
        String contactOutcome,
        String interviewerId,
        String interviewerFirstName,
        String interviewerLastName,
        Boolean viewed,
        String lastComment
) {
}
