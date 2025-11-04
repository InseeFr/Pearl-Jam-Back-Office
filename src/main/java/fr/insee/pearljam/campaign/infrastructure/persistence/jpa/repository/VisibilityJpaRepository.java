package fr.insee.pearljam.campaign.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.campaign.domain.model.CampaignVisibility;
import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.VisibilityDB;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.campaign.infrastructure.persistence.jpa.entity.VisibilityDBId;

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
		SELECT vi FROM VisibilityDB vi
		INNER JOIN SurveyUnit su ON su.campaign.id = vi.campaign.id
		WHERE su.organizationUnit.id = vi.organizationUnit.id
		AND su.id IN (:surveyUnitIds)""")
	List<VisibilityDB> findAllVisibilityBySurveyUnitIds(@Param("surveyUnitIds") List<String> surveyUnitIds);

	@Query(value = """
		SELECT new fr.insee.pearljam.campaign.domain.model.CampaignVisibility(
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

	List<VisibilityDB> findByCampaignId(String campaignId);
}
