package fr.insee.pearljam.domain.campaign.stub;

import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;

import java.util.List;

public class CampaignVisibilityPortStub implements CampaignVisibilityPort {

    
    List<CampaignVisibility> campaignsWithVisibility;

    public CampaignVisibilityPortStub(List<CampaignVisibility> campaignWithVisibility) {
        this.campaignsWithVisibility = campaignWithVisibility;
    }

    @Override
    public List<CampaignVisibility> findCampaignsWithVisibilityByUserAndManagementVisibility(List<String> ouIds, String userId, Long date) {
        return List.of();
    }

    @Override
    public CampaignVisibility getCampaignVisibility(String campaignId, List<String> userOUIds) {
        return campaignsWithVisibility.stream().filter(campaignVisibility -> campaignVisibility.id()
                .equals(campaignVisibility.id())).findFirst().get();
    }
}
