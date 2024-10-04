package fr.insee.pearljam.api.campaign.dto.input;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.web.annotation.NoDuplicateMediumAndType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

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
	ContactAttemptConfiguration contactAttemptConfiguration) {
}
