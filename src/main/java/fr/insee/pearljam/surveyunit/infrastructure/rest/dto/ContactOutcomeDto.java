package fr.insee.pearljam.surveyunit.infrastructure.rest.dto;

import fr.insee.pearljam.campaign.domain.model.ContactOutcomeType;
import fr.insee.pearljam.surveyunit.domain.model.ContactOutcome;

/**
 * Record representing a ContactOutcomeDto
 *
 * @param date                         The date of the contactOutcome.
 * @param type                         The type of the contactOutcome.
 * @param totalNumberOfContactAttempts The total number of contact attempts of the contactOutcome.
 */
public record ContactOutcomeDto(Long date, ContactOutcomeType type, Integer totalNumberOfContactAttempts) {



	/**
	 * Converts a ContactOutcomeDto to a ContactOutcome model.
	 *
	 * @param surveyUnitId The ID of the survey unit associated with the comment.
	 * @param contactOutcomeDto   The ContactOutcomeDto to convert.
	 * @return A new ContactOutcome model instance.
	 */
	public static ContactOutcome toModel(String surveyUnitId, ContactOutcomeDto contactOutcomeDto) {
		if(contactOutcomeDto==null){
			return null;
		}
		return new ContactOutcome(null,contactOutcomeDto.date(),contactOutcomeDto.type(), contactOutcomeDto.totalNumberOfContactAttempts(), surveyUnitId);
	}

	/**
	 * Converts a Comment model to a CommentDto.
	 *
	 * @param contactOutcome The ContactOutcome model to convert.
	 * @return A new ContactOutcomeDto instance.
	 */
	public static ContactOutcomeDto fromModel(ContactOutcome contactOutcome) {
		return new ContactOutcomeDto(contactOutcome.date(), contactOutcome.type(),contactOutcome.totalNumberOfContactAttempts());
	}


}
