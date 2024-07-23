package fr.insee.pearljam.infrastructure.campaign.jpa;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.domain.campaign.model.CampaignVisibility;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.VisibilityDB;
import fr.insee.pearljam.api.domain.VisibilityDBId;

public interface VisibilityJpaRepository extends JpaRepository<VisibilityDB, VisibilityDBId> {

	@Query(value = "SELECT * "
			+ "FROM visibility "
			+ "WHERE campaign_id=?1 AND organization_unit_id=?2", nativeQuery = true)
	Optional<VisibilityDB> findVisibilityByCampaignIdAndOuId(String campaignId, String organizationalUnitId);

	@Query(value = "SELECT * "
			+ "FROM visibility "
			+ "WHERE campaign_id=?1 AND organization_unit_id=?2 "
			+ "AND management_start_date<=?3 "
			+ "AND collection_start_date<=?3 "
			+ "AND collection_end_date>?3", nativeQuery = true)
	Optional<VisibilityDB> findVisibilityInCollectionPeriod(String campaignId, String organizationalUnitId, Long date);

	@Query(value = "SELECT * "
			+ "FROM visibility "
			+ "WHERE campaign_id=:campaignId AND organization_unit_id IN (:ouIds) "
			+ "AND management_start_date<=:date "
			+ "AND collection_start_date<=:date "
			+ "AND collection_end_date>:date", nativeQuery = true)
	List<VisibilityDB> findVisibilityInCollectionPeriodForOUs(@Param("campaignId") String campaignId,
															  @Param("ouIds") List<String> ouIds,
															  @Param("date") Long date);

	@Query(value = "SELECT vi "
			+ "FROM VisibilityDB vi "
			+ "INNER JOIN SurveyUnit su ON su.campaign.id = vi.campaign.id "
			+ "WHERE su.id=?1 AND su.organizationUnit.id = vi.organizationUnit.id")
	VisibilityDB getVisibilityBySurveyUnitId(String surveyUnitId);

	@Query(value = "SELECT vi "
			+ "FROM VisibilityDB vi "
			+ "INNER JOIN SurveyUnit su ON su.campaign.id = vi.campaign.id "
			+ "WHERE su.organizationUnit.id = vi.organizationUnit.id "
			+ "AND su.id IN (:SUids)")
	List<VisibilityDB> findAllVisibilityBySurveyUnitIds(@Param("SUids") List<String> surveyUnitIds);

	@Query(value = "SELECT new fr.insee.pearljam.domain.campaign.model.CampaignVisibility(MIN(vi.managementStartDate), MIN(vi.interviewerStartDate), MIN(vi.identificationPhaseStartDate), MIN(vi.collectionStartDate), MAX(vi.collectionEndDate), MAX(vi.endDate)) "
			+ "FROM VisibilityDB vi "
			+ "INNER JOIN SurveyUnit su ON su.campaign.id = vi.campaign.id "
			+ "WHERE vi.campaign.id=:campaignId AND vi.organizationUnit.id IN (:OUids)")
	CampaignVisibility findVisibilityByCampaignId(@Param("campaignId") String campaignId,
												  @Param("OUids") List<String> organizationalUnitIds);

	List<VisibilityDB> findByCampaignId(String campaignId);

	Optional<VisibilityDB> findByCampaignIdIgnoreCaseAndOrganizationUnitIdIgnoreCase(String campaignId,
																					 String organizationalUnitId);
}
