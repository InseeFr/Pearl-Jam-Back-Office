package fr.insee.pearljam.infrastructure.campaign.jpa;

import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunicationTemplateJpaRepository extends
    JpaRepository<CommunicationTemplateDB, Long> {

  @Query("""
      SELECT c FROM CommunicationTemplateDB c
      WHERE c.communicationTemplateDBId.meshuggahId = ?2
      AND c.communicationTemplateDBId.campaignId = ?1
      AND c.campaign.id = ?1""")
  Optional<CommunicationTemplateDB> findCommunicationTemplate(String campaignId, String meshuggahId);

  @Query("""
      SELECT c FROM CommunicationTemplateDB c
      WHERE c.campaign.id = ?1
      AND c.communicationTemplateDBId.campaignId = ?1""")
  List<CommunicationTemplateDB> findCommunicationTemplates(String campaignId);
}
