package fr.insee.pearljam.infrastructure.persistence.campaign.adapter;

import fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility;
import fr.insee.pearljam.domain.campaign.port.out.CampaignVisibilityPort;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class CampaignVisibilityDaoAdapter implements CampaignVisibilityPort {

    private final EntityManager em;

    private static final String JPQL_CAMPAIGNS_WITH_VISIBILITY = """
        SELECT new fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility(
            camp.id,
            camp.label,
            camp.email,
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
        GROUP BY camp.id, camp.label, camp.email
        """;

    private static final String JPQL_CAMPAIGN_VISIBILITY = """
        SELECT new fr.insee.pearljam.domain.campaign.readmodel.CampaignVisibility(
            vi.campaign.id,
            vi.campaign.label,
            vi.campaign.email,
            MIN(vi.managementStartDate),
            MIN(vi.interviewerStartDate),
            MIN(vi.identificationPhaseStartDate),
            MIN(vi.collectionStartDate),
            MAX(vi.collectionEndDate),
            MAX(vi.endDate)
        )
        FROM VisibilityDB vi
        WHERE vi.campaign.id=:campaignId
        AND vi.organizationUnit.id IN (:orgUnitIds)
        GROUP BY vi.campaign.id, vi.campaign.label, vi.campaign.email
        """;

    @Override
    public List<CampaignVisibility> findCampaignsWithVisibilityByUserAndManagementVisibility(
            List<String> ouIds, String userId, Long date) {
        return em.createQuery(JPQL_CAMPAIGNS_WITH_VISIBILITY, CampaignVisibility.class)
                .setParameter("ouIds", ouIds)
                .setParameter("userId", userId)
                .setParameter("date", date)
                .getResultList();
    }

    @Override
    public CampaignVisibility getCampaignVisibility(String campaignId, List<String> orgUnitIds) {
        return em.createQuery(JPQL_CAMPAIGN_VISIBILITY, CampaignVisibility.class)
                .setParameter("campaignId", campaignId)
                .setParameter("orgUnitIds", orgUnitIds)
                .getSingleResult();
    }
}
