package fr.insee.pearljam.domain.campaign.port.in;

import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;

import java.util.List;

public interface CampaignVisibilityPort {
    List<CampaignWithVisibility> findCampaignsWithVisibilityByUserAndManagementVisibility(List<String> ouIds, String userId, Long date);

    CampaignWithVisibility findCampaignVisibility(String campaignId, List<String> userOUIds, String userId);
}
