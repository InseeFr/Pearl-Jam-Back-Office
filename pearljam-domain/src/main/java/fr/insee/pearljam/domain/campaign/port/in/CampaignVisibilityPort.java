package fr.insee.pearljam.domain.campaign.port.in;

import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignVisibilityPort {
    List<CampaignWithVisibility> findCampaignsWithVisibilityByUserAndManagementVisibility(List<String> ouIds, String userId, Long date);

    CampaignWithVisibility findCampaignVisibility(String campaignId, List<String> userOUIds, String userId);
}
