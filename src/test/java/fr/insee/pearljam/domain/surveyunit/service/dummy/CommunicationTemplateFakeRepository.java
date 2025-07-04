package fr.insee.pearljam.domain.surveyunit.service.dummy;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.port.serverside.CommunicationTemplateRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommunicationTemplateFakeRepository implements CommunicationTemplateRepository {

    private List<CommunicationTemplate> communicationTemplates = new ArrayList<>();

    public void save(CommunicationTemplate communicationTemplate) {
        communicationTemplates.add(communicationTemplate);
    }

    public void clearCommunicationTemplates() {
        communicationTemplates.clear();
    }

    @Override
    public Optional<CommunicationTemplate> findCommunicationTemplate(String campaignId, String meshuggahId) {
        return communicationTemplates.stream()
            .filter(communicationTemplate ->
                communicationTemplate.meshuggahId().equals(meshuggahId))
            .findFirst();
    }

    @Override
    public List<CommunicationTemplate> findCommunicationTemplates(String campaignId) {
        return List.of();
    }
}
