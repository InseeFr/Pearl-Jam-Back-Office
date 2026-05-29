package fr.insee.pearljam.contracts.campaign.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.contracts.campaign.dto.ReferentDto;
import fr.insee.pearljam.contracts.annotation.NoDuplicateMediumAndType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignCreateDto (
		@NotBlank
		String campaign,
		@NotBlank
		String campaignLabel,
		@NotEmpty
		@Valid
		List<VisibilityCampaignCreateDto> visibilities,
		@Valid
		@NoDuplicateMediumAndType
		List<CommunicationTemplateCreateDto> communicationTemplates,
		List<ReferentDto> referents,
		String email,
		IdentificationConfiguration identificationConfiguration,
		ContactOutcomeConfiguration contactOutcomeConfiguration,
		ContactAttemptConfiguration contactAttemptConfiguration,
		Boolean sensitivity,
		Boolean collectNextContacts) {

	public CampaignCreateDto {
		// Defaults for nullable attributes
		if (collectNextContacts == null) collectNextContacts = Boolean.FALSE;
		if (sensitivity == null) sensitivity = Boolean.FALSE;

		if (communicationTemplates == null) communicationTemplates = List.of();
	}

}
