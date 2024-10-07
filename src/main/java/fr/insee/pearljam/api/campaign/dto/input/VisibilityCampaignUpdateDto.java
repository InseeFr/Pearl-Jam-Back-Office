package fr.insee.pearljam.api.campaign.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;

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
		String organizationalUnit,
		Boolean useLetterCommunication,
		String mail,
		String tel
) {
	public static Visibility toModel(@NonNull VisibilityCampaignUpdateDto visibility, String campaignId) {
		return new Visibility(campaignId,
				visibility.organizationalUnit(),
				visibility.managementStartDate(),
				visibility.interviewerStartDate(),
				visibility.identificationPhaseStartDate(),
				visibility.collectionStartDate(),
				visibility.collectionEndDate(),
				visibility.endDate(),
				visibility.useLetterCommunication(),
				visibility.mail(),
				visibility.tel());
	}

	public static List<Visibility> toModel(List<VisibilityCampaignUpdateDto> visibilitiesDto, String campaignId) {
		return visibilitiesDto.stream()
				.map(visibilityDto -> toModel(visibilityDto, campaignId))
				.toList();
	}
}
