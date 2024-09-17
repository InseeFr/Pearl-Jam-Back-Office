package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;
import fr.insee.pearljam.domain.campaign.port.userside.CommunicationInformationService;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;

import java.util.List;

public class CommunicationInformationFakeService implements CommunicationInformationService {
    @Override
    public List<CommunicationInformation> findCommunicationInformations(String campaignId) throws CampaignNotFoundException {
        return List.of();
    }

    @Override
    public void setCommunicationInformations(List<CommunicationInformation> communicationInformations, Campaign campaign) throws OrganizationalUnitNotFoundException, CampaignNotFoundException {
        // not used at this moment
    }

    @Override
    public void updateCommunicationInformation(CommunicationInformation communicationInformationToUpdate) {
        // not used at this moment
    }
}
