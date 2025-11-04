package fr.insee.pearljam.campaign.infrastructure.rest.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.campaign.domain.model.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * A Data Transfer Object for representing visibility context information.
 *
 * @param collectionStartDate          Collection start date of the visibility
 * @param collectionEndDate            Collection end date of the visibility
 * @param identificationPhaseStartDate Identification phase start date of the visibility
 * @param interviewerStartDate         Interviewer start date of the visibility
 * @param managementStartDate          Manager start date of the visibility
 * @param endDate                      End date of the visibility
 * @param organizationalUnit           Organizational unit of the visibility
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VisibilityCampaignCreateDto(
		@NotNull
		Long managementStartDate,
		@NotNull
		Long interviewerStartDate,
		@NotNull
		Long identificationPhaseStartDate,
		@NotNull
		Long collectionStartDate,
		@NotNull
		Long collectionEndDate,
		@NotNull
		Long endDate,
		@NotBlank
		String organizationalUnit,
		@NotNull
		@Schema(description = "Allow letter sending feature", defaultValue = "false")
		Boolean useLetterCommunication,
		@NotNull
		String mail,
		@NotNull
		String tel
) {

	public static Visibility toModel(@NonNull VisibilityCampaignCreateDto visibility, String campaignId) {
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

	public static List<Visibility> toModel(List<VisibilityCampaignCreateDto> visibilities, String campaignId) {
		return visibilities.stream()
				.map(visibility -> VisibilityCampaignCreateDto.toModel(visibility, campaignId))
				.toList();
	}
}
