package fr.insee.pearljam.infrastructure.persistence.count.adapter;

import fr.insee.pearljam.domain.surveyunit.model.count.InterviewerCount;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerCountRepository;
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
            SELECT new fr.insee.pearljam.domain.surveyunit.model.count.InterviewerCount(
                interv.id,
                interv.firstName,
                interv.lastName,
                COUNT(su.interviewer)
            )
            FROM SurveyUnitDB su
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
