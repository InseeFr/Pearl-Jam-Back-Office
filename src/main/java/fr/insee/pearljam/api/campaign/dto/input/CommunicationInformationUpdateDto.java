package fr.insee.pearljam.api.campaign.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;
import lombok.NonNull;

import java.util.List;

public record CommunicationInformationUpdateDto(
        @JsonProperty(required = true)
        String address,
        @JsonProperty(required = true)
        String mail,
        @JsonProperty(required = true)
        String tel) {

    public static CommunicationInformation toModel(CommunicationInformationUpdateDto communicationInformation,
                                                   @NonNull String campaignId,
                                                   @NonNull String ouId) {
        if(communicationInformation == null) {
            return null;
        }
        return new CommunicationInformation(
                campaignId,
                ouId,
                communicationInformation.address(),
                communicationInformation.mail(),
                communicationInformation.tel());
    }

    public static List<CommunicationInformation> toModel(List<CommunicationInformationUpdateDto> communicationsInformations,
                                                         @NonNull String campaignId,
                                                         @NonNull String ouId) {
        return communicationsInformations.stream()
                .map(communicationInformations -> toModel(communicationInformations, campaignId, ouId))
                .toList();
    }
}
