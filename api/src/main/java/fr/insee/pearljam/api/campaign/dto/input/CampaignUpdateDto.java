package fr.insee.pearljam.api.campaign.dto.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * A Data Transfer Object for updating campaign information.
 *
 * @param campaignLabel The label of the campaign.
 * @param visibilities A list of visibility context DTOs.
 * @param referents A list of referent DTOs.
 * @param email The email associated with the campaign.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignUpdateDto(
        @NotBlank
        String campaignLabel,
        @Valid
        List<VisibilityCampaignUpdateDto> visibilities,
        List<ReferentDto> referents,
        String email,
        ContactOutcomeConfiguration contactOutcomeConfiguration,
        ContactAttemptConfiguration contactAttemptConfiguration,
        Boolean collectNextContacts
) {

        public CampaignUpdateDto {
                if (collectNextContacts == null) collectNextContacts = Boolean.FALSE;
        }

}