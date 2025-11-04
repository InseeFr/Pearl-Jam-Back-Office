package fr.insee.pearljam.campaign.infrastructure.persistence.jpa.repository;

import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.CommunicationTemplateDB;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunicationTemplateJpaRepository extends
    JpaRepository<CommunicationTemplateDB, Long> {

  @Query("""
      SELECT c FROM CommunicationTemplateDB c
      WHERE c.communicationTemplateDBId.campaignId = ?1
      AND c.communicationTemplateDBId.meshuggahId = ?2""")
  Optional<CommunicationTemplateDB> findCommunicationTemplate(String campaignId, String meshuggahId);

  @Query("""
    SELECT c FROM CommunicationTemplateDB c
    LEFT JOIN FETCH c.campaign
    WHERE c.communicationTemplateDBId.campaignId = ?1
    """)
  List<CommunicationTemplateDB> findCommunicationTemplates(String campaignId);
}
