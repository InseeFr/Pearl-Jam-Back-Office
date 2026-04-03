package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.campaign.port.out.CampaignOrganizationRepository;
import fr.insee.pearljam.domain.campaign.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class CampaignOrganizationDaoAdapter implements CampaignOrganizationRepository {

    private final EntityManager em;

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

    private static final String JPQL_REFERENTS =
            """
            SELECT new fr.insee.pearljam.domain.reporting.readmodel.Referent(
                r.firstName,
                r.lastName,
                r.phoneNumber,
                r.role
            )
            FROM ReferentDB r
            WHERE r.campaign.id = :campaignId
            """;

    @Override
    public CampaignWithVisibility findCampaignVisibility(String campaignId, List<String> orgUnitIds, String userId) {
        return em.createQuery(JPQL_CAMPAIGN_VISIBILITY, CampaignWithVisibility.class)
                .setParameter("campaignId", campaignId)
                .setParameter("orgUnitIds", orgUnitIds)
                .getSingleResult();
    }

    @Override
    public List<Referent> getReferents(String campaignId) {
        return em.createQuery(JPQL_REFERENTS, Referent.class)
                .setParameter("campaignId", campaignId)
                .getResultList();
    }
}
