package fr.insee.pearljam.campaign.infrastructure.persistence.jpa.adapter;

import fr.insee.pearljam.campaign.domain.model.communication.CommunicationTemplate;
import fr.insee.pearljam.campaign.domain.port.serverside.CommunicationTemplateRepository;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.CommunicationTemplateDB;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.repository.CommunicationTemplateJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

  @Override
  public Optional<CommunicationTemplate> findCommunicationTemplate(String campaignId, String meshuggahId) {
    return communicationTemplateRepository
        .findCommunicationTemplate(campaignId, meshuggahId)
        .map(CommunicationTemplateDB::toModel);
  }
}
