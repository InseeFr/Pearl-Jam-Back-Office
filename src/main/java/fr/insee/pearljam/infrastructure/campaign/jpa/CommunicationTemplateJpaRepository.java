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
      WHERE c.id = ?1
      AND c.campaign.id = ?2""")
  Optional<CommunicationTemplateDB> findCommunicationTemplate(Long communicationTemplateId,
      String campaignId);

  @Query("""
      SELECT c FROM CommunicationTemplateDB c
      WHERE c.campaign.id = ?1""")
  List<CommunicationTemplateDB> findCommunicationTemplates(String campaignId);
}
