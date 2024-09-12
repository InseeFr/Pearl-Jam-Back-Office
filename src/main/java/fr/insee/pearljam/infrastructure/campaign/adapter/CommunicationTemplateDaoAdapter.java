package fr.insee.pearljam.infrastructure.campaign.adapter;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.port.serverside.CommunicationTemplateRepository;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.campaign.jpa.CommunicationTemplateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommunicationTemplateDaoAdapter implements CommunicationTemplateRepository {

    private final CommunicationTemplateJpaRepository communicationTemplateRepository;

    @Override
    public List<CommunicationTemplate> findCommunicationTemplates(String campaignId) {
        List<CommunicationTemplateDB> communicationTemplates = communicationTemplateRepository
                .findCommunicationTemplates(campaignId);
        return CommunicationTemplateDB.toModel(communicationTemplates);
    }
}
