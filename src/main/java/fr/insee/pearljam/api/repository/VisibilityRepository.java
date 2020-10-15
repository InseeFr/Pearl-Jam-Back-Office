package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.Visibility;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;

public interface VisibilityRepository extends JpaRepository<Visibility, String> {
	
	@Query(value = "SELECT * "
			+ "FROM visibility "
			+ "WHERE campaign_id=?1 AND organization_unit_id=?2", nativeQuery = true)
	Optional<Visibility> findVisibilityByCampaignIdAndOuId(String campaignId, String organizationalUnitId);
	
	@Query(value = "SELECT new fr.insee.pearljam.api.dto.visibility.VisibilityDto(vi.managementStartDate, vi.interviewerStartDate, vi.identificationPhaseStartDate, vi.collectionStartDate, vi.collectionEndDate, vi.endDate) "
			+ "FROM Visibility vi " 
			+ "INNER JOIN SurveyUnit su ON su.campaign.id = vi.campaign.id "
			+ "WHERE su.id=?1")
	List<VisibilityDto> findVisibilityBySurveyUnitId(String surveyUnitId);

	@Query(value = "SELECT new fr.insee.pearljam.api.dto.visibility.VisibilityDto(vi.managementStartDate, vi.interviewerStartDate, vi.identificationPhaseStartDate, vi.collectionStartDate, vi.collectionEndDate, vi.endDate) "
			+ "FROM Visibility vi " 
			+ "INNER JOIN SurveyUnit su ON su.campaign.id = vi.campaign.id "
			+ "INNER JOIN Interviewer intw ON intw.organizationUnit.id = vi.organizationUnit.id "
			+ "WHERE su.id=?1 AND LOWER(intw.id) LIKE LOWER(concat('%', concat(?2, '%')))")
	VisibilityDto findVisibilityBySurveyUnitIdAndUserId(String surveyUnitId, String userId);
	
	@Query(value = "SELECT new fr.insee.pearljam.api.dto.visibility.VisibilityDto(MIN(vi.managementStartDate), MIN(vi.interviewerStartDate), MIN(vi.identificationPhaseStartDate), MIN(vi.collectionStartDate), MAX(vi.collectionEndDate), MAX(vi.endDate)) "
			+ "FROM Visibility vi " 
			+ "INNER JOIN SurveyUnit su ON su.campaign.id = vi.campaign.id "
			+ "WHERE vi.campaign.id=:campaignId AND (vi.organizationUnit.id IN (:OUids) OR 'GUEST' IN (:OUids))")
	VisibilityDto findVisibilityByCampaignId(@Param("campaignId") String campaignId,
			@Param("OUids") List<String> organizationalUnitIds);
}
