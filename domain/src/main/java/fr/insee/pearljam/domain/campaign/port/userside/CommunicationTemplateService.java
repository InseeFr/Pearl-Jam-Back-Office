package fr.insee.pearljam.domain.campaign.port.userside;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;

import java.util.List;

public interface CommunicationTemplateService {

    /**
     * @param campaignId campaign id
     * @return the communication templates for a campaign
     */
    List<CommunicationTemplate> findCommunicationTemplates(String campaignId);
}
