package fr.insee.pearljam.infrastructure.campaign.jpa;

import fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.count.model.CommunicationRequestCount;
import fr.insee.pearljam.infrastructure.surveyunit.entity.CommunicationRequestDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

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
    SELECT new fr.insee.pearljam.api.dto.interviewer.InterviewerCountDto(i.id, COUNT(DISTINCT su.id))
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


  @Query(value = """
          SELECT
              su.campaign_id AS campaignId,
              SUM(CASE WHEN ct.type = 'NOTICE' THEN 1 ELSE 0 END) AS noticeCount,
              SUM(CASE WHEN ct.type = 'REMINDER' THEN 1 ELSE 0 END) AS reminderCount
          FROM communication_request cr
          JOIN survey_unit su ON su.id = cr.survey_unit_id
          JOIN communication_template ct
               ON ct.campaign_id = cr.campaign_id
              AND ct.meshuggah_id = cr.meshuggah_id
          WHERE su.campaign_id IN (:campaignIds)
            AND su.organization_unit_id IN (:ouIds)
            AND EXISTS (
                SELECT 1
                FROM communication_request_status crs
                WHERE crs.communication_request_id = cr.id
                  AND (crs.date <= :date OR :date < 0)
            )
          GROUP BY su.campaign_id
          """, nativeQuery = true)
  List<CommunicationRequestCount> getCommRequestCountByCampaigns(
          @Param("campaignIds") List<String> campaignIds,
          @Param("ouIds") List<String> ouIds,
          @Param("date") Long date);


  @Query(value = """
          SELECT
              su.organization_unit_id AS entityId,
              COUNT(DISTINCT CASE WHEN ctd.type = 'NOTICE' THEN su.id END) AS noticeCount,
              COUNT(DISTINCT CASE WHEN ctd.type = 'REMINDER' THEN su.id END) AS reminderCount
          FROM communication_request crd
          JOIN survey_unit su ON su.id = crd.survey_unit_id
          JOIN campaign c ON c.id = su.campaign_id
          JOIN visibility v ON v.campaign_id = c.id
          JOIN communication_template ctd
              ON ctd.campaign_id = crd.campaign_id AND ctd.meshuggah_id = crd.meshuggah_id
          JOIN communication_request_status s ON s.communication_request_id = crd.id
          WHERE crd.campaign_id = :campaignId
            AND s.date < :dateToUse
            AND v.end_date > :dateToUse
            AND v.management_start_date < :dateToUse
            AND s.status = 'READY'
            AND su.organization_unit_id IN (:ouIds)
          GROUP BY su.organization_unit_id
          """, nativeQuery = true)
  List<CommunicationRequestCount> getCommRequestCountByCampaignAndOus(@Param("campaignId") String campaignId,
                                                                      @Param("ouIds") List<String> ouIds,
                                                                      @Param("dateToUse") Long dateToUse);


}
