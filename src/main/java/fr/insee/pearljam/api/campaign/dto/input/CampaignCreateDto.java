package fr.insee.pearljam.api.campaign.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.web.annotation.NoDuplicateMediumAndType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.Nullable;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignCreateDto (
		@Nullable
		String id,
		@NotBlank
		String campaign,
		@NotBlank
		String campaignLabel,
		@NotEmpty
		@Valid
		List<VisibilityCampaignCreateDto> visibilities,
		@Valid
		@NoDuplicateMediumAndType
		@Schema(description = "List of communication templates", defaultValue = "[]")
		List<CommunicationTemplateCreateDto> communicationTemplates,
		List<ReferentDto> referents,
		String email,
		IdentificationConfiguration identificationConfiguration,
		ContactOutcomeConfiguration contactOutcomeConfiguration,
		ContactAttemptConfiguration contactAttemptConfiguration,
		@Schema(description = "Is campaign data sensitive", defaultValue = "false")
		Boolean sensitivity) {

	public CampaignCreateDto(String campaign, String campaignLabel, List<VisibilityCampaignCreateDto> visibilities, List<CommunicationTemplateCreateDto> communicationTemplates, List<ReferentDto> referents, String email, IdentificationConfiguration identificationConfiguration, ContactOutcomeConfiguration contactOutcomeConfiguration, ContactAttemptConfiguration contactAttemptConfiguration, Boolean sensitivity) {
		this("", campaign, campaignLabel, visibilities, communicationTemplates, referents, email, identificationConfiguration, contactOutcomeConfiguration, contactAttemptConfiguration, sensitivity);
	}
}
