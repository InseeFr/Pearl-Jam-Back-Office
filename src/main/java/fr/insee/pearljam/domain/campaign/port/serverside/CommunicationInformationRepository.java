package fr.insee.pearljam.domain.campaign.port.serverside;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;

import java.util.List;
import java.util.Optional;

public interface CommunicationInformationRepository {

    /**
     *
     * @param campaignId campaign id
     * @param organizationalUnitId organizational unit id
     * @return the communication information for a campaign and an organizational unit
     */
    Optional<CommunicationInformation> findCommunicationInformation(String campaignId, String organizationalUnitId);

    /**
     *
     * @param campaignId campaign id
     * @return the communication informations of a campaign
     */
    List<CommunicationInformation> findCommunicationInformations(String campaignId);

    /**
     * Update a communication information
     * @param communicatioInformationToUpdate the communication information to update
     */
    void update(CommunicationInformation communicatioInformationToUpdate);

    /**
     * set the communication informations to a campaign
     * @param communicationInformations list of communication informations to set
     * @param campaign campaign
     */
    void setCommunicationInformations(List<CommunicationInformation> communicationInformations, Campaign campaign) throws OrganizationalUnitNotFoundException;
}
