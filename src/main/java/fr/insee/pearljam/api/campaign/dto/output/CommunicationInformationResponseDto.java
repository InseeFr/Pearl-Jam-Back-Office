package fr.insee.pearljam.api.campaign.dto.output;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;

import java.util.List;

public record CommunicationInformationResponseDto(
        String organizationalUnit,
        String address,
        String mail,
        String tel) {

    public static List<CommunicationInformationResponseDto> fromModel(List<CommunicationInformation> communications) {
        return communications.stream()
                .map(communicationInformation -> new CommunicationInformationResponseDto(
                        communicationInformation.organizationalUnitId(),
                        communicationInformation.address(),
                        communicationInformation.mail(),
                        communicationInformation.tel()
                ))
                .toList();
    }
}
