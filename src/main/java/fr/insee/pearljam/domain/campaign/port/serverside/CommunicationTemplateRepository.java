package fr.insee.pearljam.domain.campaign.port.serverside;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;

import java.util.Optional;
import java.util.List;

public interface CommunicationTemplateRepository {
    /**
     *
     * @param communicationTemplateId communication template id
     * @param campaignId campaign id
     * @return the communication template
     */
    Optional<CommunicationTemplate> findCommunicationTemplate(Long communicationTemplateId, String campaignId);

    /**
     *
     * @param campaignId campaign id
     * @return list of communication templates for a campaign
     */
    List<CommunicationTemplate> findCommunicationTemplates(String campaignId);
}
