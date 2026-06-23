package fr.insee.pearljam.domain.campaign.port.in;

public interface CampaignOrganizationPort {
     <T> T getCampaignOrganization(String userId, String campaignId, CampaignOrganizationStatsPresenter<T> presenter);
}