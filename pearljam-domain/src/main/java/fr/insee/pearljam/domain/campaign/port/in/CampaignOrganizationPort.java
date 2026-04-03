package fr.insee.pearljam.domain.campaign.port.in;

import fr.insee.pearljam.domain.campaign.model.CampaignOrganization;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;

public interface CampaignOrganizationPort {
     CampaignOrganization getCampaignOrganizations(String userId, String campaignId) throws CampaignNotFoundException;
}
