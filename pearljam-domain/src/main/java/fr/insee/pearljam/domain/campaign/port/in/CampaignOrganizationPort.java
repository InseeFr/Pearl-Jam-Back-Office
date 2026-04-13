package fr.insee.pearljam.domain.campaign.port.in;

import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;

public interface CampaignOrganizationPort {
     <T> T getCampaignOrganization(String userId, String campaignId, CampaignOrganizationStatsPresenter<T> presenter) throws CampaignNotFoundException;
}