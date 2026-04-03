package fr.insee.pearljam.domain.campaign.stub;

import fr.insee.pearljam.domain.campaign.port.in.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;

import java.util.List;

public class CampaignVibilityPortStub implements CampaignVisibilityPort {

    CampaignWithVisibility campaignWithVisibility;

    public CampaignVibilityPortStub(CampaignWithVisibility campaignWithVisibility) {
        this.campaignWithVisibility = campaignWithVisibility;
    }

    @Override
    public List<CampaignWithVisibility> findCampaignsWithVisibilityByUserAndManagementVisibility(List<String> ouIds, String userId, Long date) {
        return List.of();
    }

    @Override
    public CampaignWithVisibility findCampaignVisibility(String campaignId, List<String> userOUIds, String userId) {
        return campaignWithVisibility;
    }
}
