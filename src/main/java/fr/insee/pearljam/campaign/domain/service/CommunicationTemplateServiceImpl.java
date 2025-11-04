package fr.insee.pearljam.campaign.domain.service;

import fr.insee.pearljam.campaign.domain.model.communication.CommunicationTemplate;
import fr.insee.pearljam.campaign.domain.port.serverside.CommunicationTemplateRepository;
import fr.insee.pearljam.campaign.domain.port.userside.CommunicationTemplateService;
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