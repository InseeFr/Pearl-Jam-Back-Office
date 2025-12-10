package fr.insee.pearljam.domain.campaign.service;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.port.serverside.CommunicationTemplateRepository;
import fr.insee.pearljam.domain.campaign.port.userside.CommunicationTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunicationTemplateServiceImpl implements CommunicationTemplateService {
    private final CommunicationTemplateRepository communicationTemplateRepository;

    @Override
    public List<CommunicationTemplate> findCommunicationTemplates(String campaignId) {
        return communicationTemplateRepository.findCommunicationTemplates(campaignId);
    }
}