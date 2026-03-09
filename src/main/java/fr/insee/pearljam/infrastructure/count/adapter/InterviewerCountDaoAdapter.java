package fr.insee.pearljam.infrastructure.count.adapter;

import fr.insee.pearljam.domain.count.model.InterviewerCount;
import fr.insee.pearljam.domain.count.port.serverside.InterviewerCountRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InterviewerCountDaoAdapter implements InterviewerCountRepository {

    private final EntityManager em;

    @Override
    public List<InterviewerCount> findCampaignInterviewers(String campaignId, List<String> organizationUnitIds) {
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
