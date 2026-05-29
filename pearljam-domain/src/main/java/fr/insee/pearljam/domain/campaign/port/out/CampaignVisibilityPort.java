package fr.insee.pearljam.domain.campaign.port.out;

import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;

import java.util.List;

public interface CampaignVisibilityPort {
    List<CampaignVisibility> findCampaignsWithVisibilityByUserAndManagementVisibility(List<String> ouIds, String userId, Long date);

    /**
     * Return a global visibility for a campaign, taking into account all visibilities for the campaign
     * and define the period the campaign is opened by checking all the visibilities of the organisational units for
     * this campaign
     * @param campaignId campaign id
     * @param userOUIds organisational unit ids
     * @return the campaign visibility
     */
    CampaignVisibility getCampaignVisibility(String campaignId, List<String> userOUIds);
}
