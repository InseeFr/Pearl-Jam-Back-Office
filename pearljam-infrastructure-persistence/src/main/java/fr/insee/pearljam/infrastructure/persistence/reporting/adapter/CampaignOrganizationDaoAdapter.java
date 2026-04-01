package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.campaign.port.out.CampaignOrganizationRepository;
import fr.insee.pearljam.domain.reporting.readmodel.Interviewer;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import fr.insee.pearljam.domain.reporting.readmodel.SurveyUnitsCampaignOrganization;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignWithVisibility;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class CampaignOrganizationDaoAdapter implements CampaignOrganizationRepository {

    private final EntityManager em;

    // Non-national: direct visibility row for the user's org unit
    private static final String JPQL_CAMPAIGN_VISIBILITY =
    """
    SELECT new CampaignDateData(
            c.id,
            c.label,
            MIN(v.managementStartDate),
    MIN(v.collectionStartDate),
    MAX(v.collectionEndDate),
    MAX(v.endDate)
    )
    FROM CampaignDB c
    JOIN VisibilityDB v ON v.campaign.id = c.id
    WHERE c.id = :campaignId
    AND v.organizationUnit.id IN :orgUnitIds
    GROUP BY c.id, c.label
    """;

    private static final String JPQL_REFERENTS =
    """
    SELECT new Referent(
        r.firstName,
        r.lastName,
        r.phoneNumber,
        r.role
    )
    FROM ReferentDB r
    WHERE r.campaign.id = :campaignId
    """;


    private static final String JPQL_INTERVIEWERS =
    """
    SELECT new Interviewer(
        su.interviewer.id,
        su.interviewer.lastName,
        su.interviewer.firstName,
        COUNT(su),
        SUM(CASE WHEN su.interviewer IS NULL THEN 1L ELSE 0L END),
        SUM(CASE WHEN s.type = 'CLO' THEN 1L ELSE 0L END)
    )
    FROM SurveyUnitDB su
    LEFT JOIN su.interviewer
    LEFT JOIN su.states s ON s.date = (
        SELECT MAX(s2.date) FROM StateDB s2 WHERE s2.surveyUnit.id = su.id
    )
    WHERE su.campaign.id = :campaignId
      AND su.organizationUnit.id IN :orgUnitIds
    GROUP BY su.interviewer.id, su.interviewer.lastName, su.interviewer.firstName
    """;

    @Override
    public CampaignWithVisibility findCampaignVisibility(String campaignId) {
        return em.createQuery(JPQL_CAMPAIGN_VISIBILITY, CampaignWithVisibility.class)
                .setParameter("userId", campaignId)
                .getSingleResult();
    }

    @Override
    public SurveyUnitsCampaignOrganization getSurveyUnitsCampaignOrganizations() {
        //TODO: tableau david?
        return null;

    }

    @Override
    public List<Referent> getReferents(String campaignId) {
          return em.createQuery(JPQL_REFERENTS, Referent.class)
                .setParameter("campaignId", campaignId).getResultList();
    }

    @Override
    public List<Interviewer> getInterviewers(String campaignId) {
        return em.createQuery(JPQL_INTERVIEWERS, Interviewer.class)
                .setParameter("campaignId", campaignId).getResultList();
    }
}
