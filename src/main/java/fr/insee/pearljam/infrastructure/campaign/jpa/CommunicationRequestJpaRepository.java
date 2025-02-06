package fr.insee.pearljam.infrastructure.campaign.jpa;

import fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunicationRequestJpaRepository extends
    JpaRepository<CommunicationRequestDB, Long> {

  /**
   * Compte le nombre de demandes de communication pour une campagne et un type donné.
   */
  @Query("""
      SELECT COUNT(ct) FROM communication_request cr
      INNER JOIN cr.communicationTemplate ct
      INNER JOIN cr.status crs
      WHERE ct.campaign.id = :campaignId
      AND ct.type = :type
      AND crs.status = SUBMITTED
      """)
  Long getCommunicationRequestCountByCampaignAndCommunicationType(String campaignId,
      CommunicationType type);

  /**
   * Compte le nombre de demandes de communication par enquêteur pour un type donné.
   */
  @Query("""
      SELECT new fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto(i.id, COUNT(ct))
      FROM communication_request cr
      INNER JOIN cr.surveyUnit su
      INNER JOIN su.interviewer i
      INNER JOIN cr.communicationTemplate ct
      INNER JOIN cr.status crs
      WHERE i.id IN :interviewerIds
      AND ct.type = :type
      AND crs.status = SUBMITTED
      GROUP BY i.id
      """)
  List<InterviewerCountDto> getCommunicationRequestCountByInterviewersAndCommunicationType(
      Set<String> interviewerIds, CommunicationType type);
}
