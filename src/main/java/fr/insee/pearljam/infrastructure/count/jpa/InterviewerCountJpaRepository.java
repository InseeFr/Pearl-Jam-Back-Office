package fr.insee.pearljam.infrastructure.count.jpa;

import fr.insee.pearljam.domain.count.model.InterviewerCount;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class InterviewerCountJpaRepository  {

    private EntityManager em;

    public List<InterviewerCount> findInterviewerCountByCampaignIdAndOuId(
            String campaignId,
            List<String> organizationUnitIds
    ) {
        String jpql = """
            SELECT new fr.insee.pearljam.domain.count.model.InterviewerCount(
                interv.id,
                interv.firstName,
                interv.lastName,
                COUNT(su.interviewer)
            )
            FROM SurveyUnit su
            JOIN su.interviewer interv
            WHERE su.campaign.id = :campaignId
              AND (su.organizationUnit.id IN :organizationUnitIds OR 'GUEST' IN :organizationUnitIds)
            GROUP BY interv.id, interv.firstName, interv.lastName
            """;

        return em.createQuery(jpql, InterviewerCount.class)
                .setParameter("campaignId", campaignId)
                .setParameter("organizationUnitIds", organizationUnitIds)
                .getResultList();
    }
}
