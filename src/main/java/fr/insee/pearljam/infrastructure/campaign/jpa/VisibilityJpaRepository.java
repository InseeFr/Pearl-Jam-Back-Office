package fr.insee.pearljam.infrastructure.campaign.jpa;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.infrastructure.campaign.entity.VisibilityDB;
import fr.insee.pearljam.infrastructure.campaign.entity.VisibilityDBId;

public interface VisibilityJpaRepository extends JpaRepository<VisibilityDB, VisibilityDBId> {

	@Query(value = """
		SELECT * FROM visibility
		WHERE campaign_id=?1
		AND organization_unit_id=?2""", nativeQuery = true)
	Optional<VisibilityDB> findVisibilityByCampaignIdAndOuId(String campaignId, String organizationalUnitId);

	@Query(value = """
		SELECT vi FROM VisibilityDB vi
		INNER JOIN SurveyUnit su ON su.campaign.id = vi.campaign.id
		WHERE su.id=?1
		AND su.organizationUnit.id = vi.organizationUnit.id""")
	VisibilityDB getVisibilityBySurveyUnitId(String surveyUnitId);

	@Query(value = """
		SELECT new fr.insee.pearljam.domain.campaign.model.CampaignVisibility(
		    MIN(vi.managementStartDate),
		    MIN(vi.interviewerStartDate),
		    MIN(vi.identificationPhaseStartDate),
		    MIN(vi.collectionStartDate),
		    MAX(vi.collectionEndDate),
		    MAX(vi.endDate)
		)
		FROM VisibilityDB vi
		WHERE vi.campaign.id=:campaignId
		AND vi.organizationUnit.id IN (:organizationalUnitIds)""")
	CampaignVisibility getCampaignVisibility(@Param("campaignId") String campaignId,
													   @Param("organizationalUnitIds") List<String> organizationalUnitIds);

	@Query(value = """
			SELECT
			    vi.campaign.id AS campaignId,
			    vi.campaign.label AS campaignLabel,
			    MIN(vi.managementStartDate) AS managementStartDate,
			    MAX(vi.endDate) AS endDate
			FROM VisibilityDB vi
			JOIN SurveyUnit su
			    ON su.campaign.id = vi.campaign.id
			   AND su.organizationUnit.id = vi.organizationUnit.id
			WHERE su.id IN :surveyUnitIds
			GROUP BY vi.campaign.id, vi.campaign.label""")
	List<CampaignVisibilityPeriodProjection> findCampaignsBySurveyUnitIds(List<String> surveyUnitIds);


	List<VisibilityDB> findByCampaignId(String campaignId);
}
