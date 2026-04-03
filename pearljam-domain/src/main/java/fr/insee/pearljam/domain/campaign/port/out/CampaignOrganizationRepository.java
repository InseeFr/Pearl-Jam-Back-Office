package fr.insee.pearljam.domain.campaign.port.out;

import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;

import java.util.List;

public interface CampaignOrganizationRepository {
    CampaignWithVisibility findCampaignVisibility(String campaignId, List<String> userOUIds, String userId);
    List<Referent> getReferents(String campaignId);
}
