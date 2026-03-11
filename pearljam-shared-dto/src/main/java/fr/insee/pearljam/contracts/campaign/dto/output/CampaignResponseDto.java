package fr.insee.pearljam.contracts.campaign.dto.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.contracts.campaign.dto.ReferentDto;

import java.util.List;

/**
 * A Data Transfer Object for representing campaign information.
 *
 * @param campaign The campaign identifier.
 * @param campaignLabel The label of the campaign.
 * @param visibilities A list of visibility DTOs.
 * @param referents A list of referent DTOs.
 * @param email The email associated with the campaign.
 * @param identificationConfiguration The identification configuration of the campaign.
 * @param contactOutcomeConfiguration The contact outcome configuration of the campaign.
 * @param contactAttemptConfiguration The contact attempt configuration of the campaign.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CampaignResponseDto(
        String id,
        String campaign,
        String campaignLabel,
        List<VisibilityCampaignDto> visibilities,
        List<ReferentDto> referents,
        String email,
        IdentificationConfiguration identificationConfiguration,
        ContactOutcomeConfiguration contactOutcomeConfiguration,
        ContactAttemptConfiguration contactAttemptConfiguration,
        Boolean sensitivity
) {
    public static CampaignResponseDto fromModel(String campaignId,
                                                String campaignLabel,
                                                String email,
                                                IdentificationConfiguration identificationConfiguration,
                                                ContactOutcomeConfiguration contactOutcomeConfiguration,
                                                ContactAttemptConfiguration contactAttemptConfiguration,
                                                Boolean sensitivity,
                                                List<ReferentDto> referents,
                                                List<VisibilityCampaignDto> visibilities) {
        return new CampaignResponseDto(campaignId,
                campaignId,
                campaignLabel,
                visibilities,
                referents,
                email,
                identificationConfiguration,
                contactOutcomeConfiguration,
                contactAttemptConfiguration,
                sensitivity
                );
    }
}
