package fr.insee.pearljam.domain.reporting.readmodel.progress;

public record CampaignProgress(String campaignId,
                               String campaignLabel,
                               float progressRate,
                               StatesProgress states,
                               CommunicationsProgress communications) {
}
