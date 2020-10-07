package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.Visibility;
import fr.insee.pearljam.api.dto.visibility.VisibilityDto;

public interface VisibilityRepository extends JpaRepository<Visibility, String>{
	@Query(value="SELECT MIN(collection_start_date) "
			+ "FROM visibility "
			+ "WHERE campaign_id=:campaignId AND (organization_unit_id IN (:OUids) OR 'GUEST' IN (:OUids))" , nativeQuery=true)
  Long findVisibilityStartDateByCampaignId(@Param("campaignId") String campaignId, @Param("OUids") List<String> organizationalUnitIds);
  
  @Query(value="SELECT MAX(collection_end_date) "
    + "FROM visibility "
    + "WHERE campaign_id=:campaignId AND (organization_unit_id IN (:OUids) OR 'GUEST' IN (:OUids))" , nativeQuery=true)
  Long findTreatmentEndDateByCampaignId(@Param("campaignId") String campaignId, @Param("OUids") List<String> organizationalUnitIds);

  @Query(value="SELECT * "
		    + "FROM visibility "
		    + "WHERE campaign_id=?1 AND organization_unit_id=?2" , nativeQuery=true)
  Optional<Visibility> findVisibilityByCampaignIdAndOuId(String campaignId, String organizationalUnitId);
  
  @Query(value="SELECT new fr.insee.pearljam.api.dto.visibility.VisibilityDto(vi.collectionStartDate) "
		    + "FROM Visibility vi "
		    + "INNER JOIN SurveyUnit su ON su.campaign.id = vi.campaign.id "
		    + "WHERE su.id=?1 ")
  VisibilityDto findVisibilityBySurveyUnitId(String surveyUnitId);
}
