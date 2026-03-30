package fr.insee.pearljam.domain.campaign.service;

import fr.insee.pearljam.domain.campaign.model.CampaignOrganization;
import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationPort;
import fr.insee.pearljam.domain.campaign.port.in.CampaignService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CampaignOrganizationService implements CampaignOrganizationPort {

    CampaignService campaignService;

    @Override
    public CampaignOrganization getCampaignOrganization(String campaignId) {


        return new CampaignOrganization();
    }
}
