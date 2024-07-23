package fr.insee.pearljam.api.campaign.dto.input.visibility;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.web.exception.VisibilityInvalidException;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

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
public record VisibilityCampaignUpdateDto(
		Long managementStartDate,
		Long interviewerStartDate,
		Long identificationPhaseStartDate,
		Long collectionStartDate,
		Long collectionEndDate,
		Long endDate,
		@NotBlank
		String organizationalUnit
) {
	public static final String AT_LEAST_ONE_DATE_REQUIRED_MESSAGE = "At least one date must be provided for a visibility";

	public VisibilityCampaignUpdateDto {
		if (managementStartDate == null && interviewerStartDate == null && identificationPhaseStartDate == null &&
				collectionStartDate == null && collectionEndDate == null && endDate == null) {
			throw new VisibilityInvalidException(AT_LEAST_ONE_DATE_REQUIRED_MESSAGE);
		}
	}

	public static List<Visibility> toModel(List<VisibilityCampaignUpdateDto> visibilitiesDto, String campaignId) {
		return visibilitiesDto.stream()
				.map(visibilityDto -> new Visibility(campaignId,
					visibilityDto.organizationalUnit(),
					visibilityDto.managementStartDate(),
					visibilityDto.interviewerStartDate(),
					visibilityDto.identificationPhaseStartDate(),
					visibilityDto.collectionStartDate(),
					visibilityDto.collectionEndDate(),
					visibilityDto.endDate(),
					null
				))
				.toList();
	}
}
