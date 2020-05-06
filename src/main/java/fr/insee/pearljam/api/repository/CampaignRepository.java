package fr.insee.pearljam.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
	
	Campaign findDtoById(String id);
	
	/**
	* This method retrieve all Campaigns for a SurveyUnitId 
	* 
	* @return Id of {@link Campaign}
	*/
	@Query(value="SELECT camp.id, camp.label, camp.collection_start_date , camp.collection_end_date "
			+ "FROM campaign camp "
			+ "JOIN  survey_unit su ON su.campaign_id = camp.id "
			+ "WHERE su.id=?", nativeQuery=true)
	CampaignDto findDtoBySurveyUnitId(String surveyUnitId);
	
}
