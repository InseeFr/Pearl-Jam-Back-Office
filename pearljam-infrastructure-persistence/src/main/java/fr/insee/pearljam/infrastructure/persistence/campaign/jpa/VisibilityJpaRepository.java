package fr.insee.pearljam.infrastructure.persistence.campaign.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.infrastructure.persistence.campaign.entity.VisibilityDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.VisibilityDBId;

public interface VisibilityJpaRepository extends JpaRepository<VisibilityDB, VisibilityDBId> {

	@Query(value = """
		SELECT * FROM visibility
		WHERE campaign_id=?1
		AND organization_unit_id=?2""", nativeQuery = true)
	Optional<VisibilityDB> findVisibilityByCampaignIdAndOuId(String campaignId, String organizationalUnitId);

	@Query(value = """
		SELECT vi FROM VisibilityDB vi
		INNER JOIN SurveyUnitDB su ON su.campaign.id = vi.campaign.id
		WHERE su.id=?1
		AND su.organizationUnit.id = vi.organizationUnit.id""")
	VisibilityDB getVisibilityBySurveyUnitId(String surveyUnitId);

	@Query(value = """
			SELECT
			    vi.campaign.id AS campaignId,
			    vi.campaign.label AS campaignLabel,
			    MIN(vi.managementStartDate) AS managementStartDate,
			    MAX(vi.endDate) AS endDate
			FROM VisibilityDB vi
			JOIN SurveyUnitDB su
			    ON su.campaign.id = vi.campaign.id
			   AND su.organizationUnit.id = vi.organizationUnit.id
			WHERE su.id IN :surveyUnitIds
			GROUP BY vi.campaign.id, vi.campaign.label""")
	List<CampaignVisibilityPeriodProjection> findCampaignsBySurveyUnitIds(List<String> surveyUnitIds);


	List<VisibilityDB> findByCampaignId(String campaignId);
}
