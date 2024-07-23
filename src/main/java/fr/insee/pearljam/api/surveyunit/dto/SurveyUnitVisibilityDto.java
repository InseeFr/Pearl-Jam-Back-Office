package fr.insee.pearljam.api.surveyunit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.domain.campaign.model.Visibility;

/**
 * A Data Transfer Object for representing visibility information.
 *
 * @param collectionStartDate         Collection start date of the visibility
 * @param collectionEndDate           Collection end date of the visibility
 * @param identificationPhaseStartDate Identification phase start date of the visibility
 * @param interviewerStartDate        Interviewer start date of the visibility
 * @param managementStartDate         Manager start date of the visibility
 * @param endDate                     End date of the visibility
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SurveyUnitVisibilityDto(
		Long managementStartDate,
		Long interviewerStartDate,
		Long identificationPhaseStartDate,
		Long collectionStartDate,
		Long collectionEndDate,
		Long endDate
) {

	public static SurveyUnitVisibilityDto fromModel(Visibility visibility) {
		return new SurveyUnitVisibilityDto(
				visibility.managementStartDate(),
				visibility.interviewerStartDate(),
				visibility.identificationPhaseStartDate(),
				visibility.collectionStartDate(),
				visibility.collectionEndDate(),
				visibility.endDate());
	}
}
