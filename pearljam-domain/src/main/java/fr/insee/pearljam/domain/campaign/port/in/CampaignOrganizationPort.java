package fr.insee.pearljam.domain.campaign.port.in;

import fr.insee.pearljam.domain.campaign.model.CampaignOrganization;

import java.util.List;

public interface CampaignOrganizationPort {
     CampaignOrganization getCampaignOrganizations(String userId, String campaignId);
}
