package fr.insee.pearljam.api.campaign.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CommunicationInformationCampaignUpdateDto(
        @NotBlank
        String organizationalUnit,
        @JsonProperty(required = true)
        String mail,
        @JsonProperty(required = true)
        String tel) {

    public static CommunicationInformation toModel(CommunicationInformationCampaignUpdateDto communicationInformation, String campaignId) {
        if(communicationInformation == null) {
            return null;
        }
        return new CommunicationInformation(
                campaignId,
                communicationInformation.organizationalUnit(),
                communicationInformation.mail(),
                communicationInformation.tel());
    }

    public static List<CommunicationInformation> toModel(List<CommunicationInformationCampaignUpdateDto> communicationsInformations, String campaignId) {
        return communicationsInformations.stream()
                .map(communicationInformations -> toModel(communicationInformations, campaignId))
                .toList();
    }
    }
