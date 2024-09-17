package fr.insee.pearljam.domain.campaign.port.serverside;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;

import java.util.List;

public interface CommunicationInformationRepository {

    /**
     *
     * @param campaignId campaign id
     * @return the communication informations of a campaign
     */
    List<CommunicationInformation> findCommunicationInformations(String campaignId);

    /**
     * Update a communication information
     * @param communicationInformationToUpdate the communication information to update
     */
    void update(CommunicationInformation communicationInformationToUpdate);

    /**
     * set the communication informations to a campaign
     * @param communicationInformations list of communication informations to set
     * @param campaign campaign
     */
    void setCommunicationInformations(List<CommunicationInformation> communicationInformations, Campaign campaign) throws OrganizationalUnitNotFoundException;
}
