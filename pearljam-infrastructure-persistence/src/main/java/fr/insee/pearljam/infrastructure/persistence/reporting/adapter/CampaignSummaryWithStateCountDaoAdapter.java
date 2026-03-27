package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.contracts.campaign.dto.CampaignDto;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.reporting.port.out.CampaignSummaryWithStateCountRepositoryPort;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignWithVisibility;
import fr.insee.pearljam.domain.reporting.readmodel.CommunicationRequestCount;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CampaignSummaryWithStateCountDaoAdapter implements CampaignSummaryWithStateCountRepositoryPort {

    private final EntityManager em;
    private final CampaignRepository campaignRepository;


	private static final String JPQL_CAMPAIGN_WITH_VISIBILITY =
		"""
		SELECT new fr.insee.pearljam.domain.reporting.readmodel.CampaignWithVisibility(
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


    @Override
    public List<CampaignWithVisibility> findByUserAndManagementVisibility(List<String> ouIds, String userId, Long currentTimestamp) {
		return em.createQuery(JPQL_CAMPAIGN_WITH_VISIBILITY, CampaignWithVisibility.class)
				.setParameter("ouIds", ouIds)
				.setParameter("userId", userId)
				.setParameter("date", currentTimestamp)
				.getResultList();
    }

}