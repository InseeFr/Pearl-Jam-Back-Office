package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.reporting.projection.StateCountProjection;
import fr.insee.pearljam.domain.reporting.port.out.CampaignProgressionRepository;
import fr.insee.pearljam.domain.surveyunit.model.count.CommunicationRequestCount;
import fr.insee.pearljam.domain.surveyunit.model.count.InterviewerCount;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class CampaignProgressionDaoAdapter implements CampaignProgressionRepository {

    private final EntityManager em;

    private static final String OU_IDS = "ouIds";

    public List<InterviewerCount> findAllDtoByOuIds(List<String> ouIds) {
            String jpql = """
                    SELECT DISTINCT new fr.insee.pearljam.contracts.campaign.dto.CampaignProjection(
                            camp.id, camp.label, camp.email,
                            camp.identificationConfiguration,
                            camp.contactOutcomeConfiguration,
                            camp.contactAttemptConfiguration,
                            camp.collectNextContacts
                    )
                    FROM CampaignDB camp
                    JOIN camp.visibilities vi
                    WHERE vi.organizationUnit.id IN :ouIds""";

            return em.createQuery(jpql, InterviewerCount.class)
                    .setParameter(OU_IDS, ouIds)
                    .getResultList();
        }

        public List<String>  findAllCampaignIdsByOuIds(List<String> userOrgUnitIds) {
            String jpql =
                    """
                    SELECT DISTINCT(campaign_id) FROM visibility WHERE "organization_unit_id IN (:OuIds)", nativeQuery = true)
                    """;

            return em.createQuery(jpql, String.class)
                    .setParameter(OU_IDS, userOrgUnitIds)
                    .getResultList();
        }

    public List<StateCountProjection> findGroupedByCampaign(
                                           @Param("campaignIds") List<String> campaignIds,
                                           @Param(OU_IDS) List<String> ouIds,
                                           @Param("date") Long date) {

            String jpql =
                    """
                    SELECT
                    su.campaign_id AS entityId,
                    SUM(CASE WHEN s.type = 'NVM' THEN 1 ELSE 0 END) AS nvmCount,
                    SUM(CASE WHEN s.type = 'NNS' THEN 1 ELSE 0 END) AS nnsCount,
                    SUM(CASE WHEN s.type = 'ANV' THEN 1 ELSE 0 END) AS anvCount,
                    SUM(CASE WHEN s.type = 'VIN' THEN 1 ELSE 0 END) AS vinCount,
                    SUM(CASE WHEN s.type = 'VIC' THEN 1 ELSE 0 END) AS vicCount,
                    SUM(CASE WHEN s.type = 'PRC' THEN 1 ELSE 0 END) AS prcCount,
                    SUM(CASE WHEN s.type = 'AOC' THEN 1 ELSE 0 END) AS aocCount,
                    SUM(CASE WHEN s.type = 'APS' THEN 1 ELSE 0 END) AS apsCount,
                    SUM(CASE WHEN s.type = 'INS' THEN 1 ELSE 0 END) AS insCount,
                    SUM(CASE WHEN s.type = 'WFT' THEN 1 ELSE 0 END) AS wftCount,
                    SUM(CASE WHEN s.type = 'WFS' THEN 1 ELSE 0 END) AS wfsCount,
                    SUM(CASE WHEN s.type = 'TBR' THEN 1 ELSE 0 END) AS tbrCount,
                    SUM(CASE WHEN s.type = 'FIN' THEN 1 ELSE 0 END) AS finCount,
                    SUM(CASE WHEN s.type = 'CLO' THEN 1 ELSE 0 END) AS cloCount,
                    SUM(CASE WHEN s.type = 'NVA' THEN 1 ELSE 0 END) AS nvaCount,
                    COUNT(1) AS total
                    FROM survey_unit su
                    JOIN (
                    SELECT survey_unit_id, MAX(date) AS max_date
                    FROM state
                    GROUP BY survey_unit_id
                    ) last_state ON su.id = last_state.survey_unit_id
                    JOIN state s
                    ON s.survey_unit_id = last_state.survey_unit_id
                    AND s.date = last_state.max_date
                    WHERE su.campaign_id IN (:campaignIds)
                    AND su.organization_unit_id IN (:ouIds)
                    AND (s.date <= :date OR :date < 0)
                    GROUP BY su.campaign_id
                    """;

            return em.createQuery(jpql, StateCountProjection.class)
                    .setParameter(OU_IDS, ouIds)
                    .setParameter("campaignIds", campaignIds)
                    .setParameter("date", date)
                    .getResultList();
        }

    public List<CommunicationRequestCount> commRequestCountsByCampaign (List<String> campaignIds,
                                                                        List<String> ouIds,
                                                                        Long date){
        String jpql ="""
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
          """;

        return em.createQuery(jpql, CommunicationRequestCount.class)
                .setParameter(OU_IDS, ouIds)
                .setParameter("campaignIds", campaignIds)
                .setParameter("date", date)
                .getResultList();
    }
}