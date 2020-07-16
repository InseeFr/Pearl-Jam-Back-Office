package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.Visibility;


public interface VisibilityRepository extends JpaRepository<Visibility, String>{
	@Query(value="SELECT vi.collection_start_date "
			+ "FROM visibility vi "
			+ "INNER JOIN public.user us ON us.organization_unit_id = vi.organization_unit_id "
			+ "WHERE vi.campaign_id=?1 AND us.id ILIKE ?2" , nativeQuery=true)
	Long findVisibilityStartDateByCampaignId(String campaignId, String userId);
	
}
