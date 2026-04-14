package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.campaign.port.out.CampaignReferentRepository;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class CampaignReferentDaoAdapter implements CampaignReferentRepository {

    private final EntityManager em;


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
    public List<Referent> getReferents(String campaignId) {
        return em.createQuery(JPQL_REFERENTS, Referent.class)
                .setParameter("campaignId", campaignId)
                .getResultList();
    }
}
