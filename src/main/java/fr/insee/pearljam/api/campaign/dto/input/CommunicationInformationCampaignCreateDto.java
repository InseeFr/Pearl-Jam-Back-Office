package fr.insee.pearljam.api.campaign.dto.input;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;
import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

public record CommunicationInformationCampaignCreateDto(
        @NotBlank
        String organizationalUnit,
        String mail,
        String tel) {
    public static CommunicationInformation toModel(@NonNull CommunicationInformationCampaignCreateDto communicationInformation,
                                                   String campaignId) {
        return new CommunicationInformation(campaignId,
                communicationInformation.organizationalUnit(),
                communicationInformation.mail(),
                communicationInformation.tel());
    }

    public static List<CommunicationInformation> toModel(List<CommunicationInformationCampaignCreateDto> communicationInformations,
                                                         String campaignId) {
        if(communicationInformations == null) {
            return new ArrayList<>();
        }

        return communicationInformations.stream()
                .map(communicationInformation -> CommunicationInformationCampaignCreateDto.toModel(communicationInformation, campaignId))
                .toList();
    }
}
