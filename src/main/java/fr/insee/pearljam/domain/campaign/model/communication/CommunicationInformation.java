package fr.insee.pearljam.domain.campaign.model.communication;

public record CommunicationInformation(
        String campaignId,
        String organizationalUnitId,
        String mail,
        String tel
) {
}
