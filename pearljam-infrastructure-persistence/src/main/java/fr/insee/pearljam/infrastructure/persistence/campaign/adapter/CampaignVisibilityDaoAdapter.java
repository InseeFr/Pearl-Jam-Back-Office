package fr.insee.pearljam.infrastructure.persistence.campaign.adapter;

import fr.insee.pearljam.domain.campaign.port.in.CampaignVisibilityPort;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CampaignVisibilityDaoAdapter implements CampaignVisibilityPort {

    private final EntityManager em;

    private static final String JPQL_CAMPAIGNS_WITH_VISIBILITY = """
            SELECT new fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility(
                camp.id,
                camp.label,
                MIN(vi.managementStartDate),
                MIN(vi.interviewerStartDate),
                MIN(vi.identificationPhaseStartDate),
                MIN(vi.collectionStartDate),
                MAX(vi.collectionEndDate),
                MAX(vi.endDate)
            )
            FROM CampaignDB camp
            JOIN camp.visibilities vi
            JOIN vi.organizationUnit ou
            WHERE vi.managementStartDate <= :date
            AND vi.endDate > :date
            AND NOT EXISTS (
                SELECT 1
                FROM UserDB u
                JOIN u.campaigns c2
                WHERE LOWER(u.id) = LOWER(:userId)
                AND c2 = camp
            )
            AND ou.id in (:ouIds)
            GROUP BY camp.id, camp.label
            """;

    private static final String JPQL_CAMPAIGN_VISIBILITY =
            """
            SELECT new fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility(
               camp.id,
               camp.label,
               MIN(vi.managementStartDate),
               MIN(vi.interviewerStartDate),
               MIN(vi.identificationPhaseStartDate),
               MIN(vi.collectionStartDate),
               MAX(vi.collectionEndDate),
               MAX(vi.endDate)
               )
               FROM CampaignDB camp
               JOIN camp.visibilities vi
               JOIN vi.organizationUnit ou
               WHERE ou.id IN (:orgUnitIds)
                 AND camp.id = :campaignId
               GROUP BY camp.id, camp.label
            """;

    @Override
    public List<CampaignWithVisibility> findCampaignsWithVisibilityByUserAndManagementVisibility(
            List<String> ouIds, String userId, Long date) {
        return em.createQuery(JPQL_CAMPAIGNS_WITH_VISIBILITY, CampaignWithVisibility.class)
                .setParameter("ouIds", ouIds)
                .setParameter("userId", userId)
                .setParameter("date", date)
                .getResultList();
    }

    @Override
    public CampaignWithVisibility findCampaignVisibility(String campaignId, List<String> orgUnitIds, String userId) {
        return em.createQuery(JPQL_CAMPAIGN_VISIBILITY, CampaignWithVisibility.class)
                .setParameter("campaignId", campaignId)
                .setParameter("orgUnitIds", orgUnitIds)
                .getSingleResult();
    }
}
