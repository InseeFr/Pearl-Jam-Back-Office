package fr.insee.pearljam.domain.campaign.port.userside;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;

import java.util.List;

public interface CommunicationInformationService {

    /**
     *
     * @param campaignId campaign id
     * @return the visibilities for the campaign
     * @throws CampaignNotFoundException if campaign not found
     */
    List<CommunicationInformation> findCommunicationInformations(String campaignId) throws CampaignNotFoundException;


    /**
     *
     * @param communicationInformations List of communication informations to set
     * @param campaign campaign where the list should be set
     * @throws OrganizationalUnitNotFoundException exception when no ou found
     * @throws CampaignNotFoundException exception when no campaign found
     */
    void setCommunicationInformations(List<CommunicationInformation> communicationInformations, Campaign campaign) throws OrganizationalUnitNotFoundException, CampaignNotFoundException;

    /**
     * update a communication information
     * @param communicationInformationToUpdate informationToUpdate communication information to update
     */
    void updateCommunicationInformation(CommunicationInformation communicationInformationToUpdate);
}
