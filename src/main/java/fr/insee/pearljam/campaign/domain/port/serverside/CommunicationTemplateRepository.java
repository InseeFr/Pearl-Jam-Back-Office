package fr.insee.pearljam.campaign.domain.port.serverside;

import fr.insee.pearljam.campaign.domain.model.communication.CommunicationTemplate;

import java.util.Optional;
import java.util.List;

public interface CommunicationTemplateRepository {
    /**
     *
     * @param communicationTemplateId communication template id
     * @param campaignId campaign id
     * @return the communication template
     */
    Optional<CommunicationTemplate> findCommunicationTemplate(String campaignId, String meshuggahId);

    /**
     *
     * @param campaignId campaign id
     * @return list of communication templates for a campaign
     */
    List<CommunicationTemplate> findCommunicationTemplates(String campaignId);
}
