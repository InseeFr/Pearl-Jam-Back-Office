package fr.insee.pearljam.campaign.infrastructure.persistence.jpa.repository;

import fr.insee.pearljam.surveyunit.infrastructure.rest.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.campaign.domain.model.communication.CommunicationType;
import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.CommunicationRequestDB;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunicationRequestJpaRepository extends
    JpaRepository<CommunicationRequestDB, Long> {

  /**
   * Counts the number of communication requests for a given campaign and communication type.
   *
   * @param campaignId The ID of the campaign.
   * @param type The type of communication.
   * @param date The reference date for filtering requests.
   * @return The number of communication requests matching the criteria.
   */
  @Query("""
       SELECT COUNT(DISTINCT su.id)
       FROM communication_request cr
       INNER JOIN cr.surveyUnit su
       INNER JOIN su.campaign c
       INNER JOIN c.visibilities vi
       INNER JOIN CommunicationTemplateDB ct ON ct.communicationTemplateDBId.campaignId = cr.communicationTemplateDBId.campaignId
                                           AND ct.communicationTemplateDBId.meshuggahId = cr.communicationTemplateDBId.meshuggahId
       INNER JOIN cr.status crs
       WHERE cr.communicationTemplateDBId.campaignId = :campaignId
       AND ct.type = :type
       AND crs.date < :date
       AND vi.endDate > :date
       AND vi.managementStartDate < :date
       AND crs.status = 'READY'
       """)
  Long getCommRequestCountByCampaignAndType(String campaignId,
      CommunicationType type, Long date);


  /**
   * Counts the number of communication requests for a campaign, communication type, and a given organization unit.
   *
   * @param campaignId The ID of the campaign.
   * @param type The type of communication.
   * @param date The reference date for filtering requests.
   * @param ouIds The list of organization unit IDs.
   * @return The number of communication requests matching the criteria.
   */
  @Query("""
      SELECT COUNT(DISTINCT su.id)
      FROM communication_request cr
      INNER JOIN cr.surveyUnit su
      INNER JOIN su.campaign c
      INNER JOIN c.visibilities vi
      INNER JOIN CommunicationTemplateDB ct ON ct.communicationTemplateDBId.campaignId = cr.communicationTemplateDBId.campaignId
                                           AND ct.communicationTemplateDBId.meshuggahId = cr.communicationTemplateDBId.meshuggahId
      INNER JOIN cr.status crs
      WHERE cr.communicationTemplateDBId.campaignId = :campaignId
      AND ct.type = :type
      AND crs.date < :date
      AND vi.endDate > :date
      AND vi.managementStartDate < :date
      AND crs.status = 'READY'
      AND su.organizationUnit.id IN (:ouIds)
      """)
  Long getCommRequestCountByCampaignTypeAndOrgaUnit(String campaignId,
      CommunicationType type, Long date, List<String> ouIds);


  /**
   * Counts the number of communication requests per interviewer for a given communication type.
   *
   * @param campaignIds The list of campaign IDs.
   * @param interviewerIds The set of interviewer IDs.
   * @param type The type of communication.
   * @param ouIds The list of organization unit IDs.
   * @param date The reference date for filtering requests.
   * @return A list of {@link InterviewerCountDto} containing interviewer IDs and their respective request counts.
   */
  @Query("""
    SELECT new fr.insee.pearljam.surveyunit.infrastructure.rest.dto.interviewer.InterviewerCountDto(i.id, COUNT(DISTINCT su.id))
    FROM communication_request cr
    INNER JOIN cr.surveyUnit su
    INNER JOIN su.campaign c
    INNER JOIN c.visibilities vi
    INNER JOIN su.interviewer i
    INNER JOIN CommunicationTemplateDB ct ON ct.communicationTemplateDBId.campaignId = cr.communicationTemplateDBId.campaignId
                                           AND ct.communicationTemplateDBId.meshuggahId = cr.communicationTemplateDBId.meshuggahId
    INNER JOIN cr.status crs
    WHERE c.id IN :campaignIds
    AND i.id IN :interviewerIds
    AND ct.type = :type
    AND crs.date < :date
    AND vi.endDate > :date
    AND vi.managementStartDate < :date
    AND crs.status = 'READY'
    AND su.organizationUnit.id IN (:ouIds)
    GROUP BY i.id
    """)
  List<InterviewerCountDto> getCommRequestCountByInterviewersAndType(
      List<String> campaignIds,
      Set<String> interviewerIds,
      CommunicationType type,
      List<String> ouIds,
      Long date);
}
