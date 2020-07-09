package fr.insee.pearljam.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;

public interface CampaignRepository extends JpaRepository<Campaign, String> {
	
	@Query(value="SELECT id "
			+ "FROM campaign ", nativeQuery=true)
	List<String> findAllIds();
	
	/**
	* This method retrieve all Id of SurveyUnits with a certain state and idInterviewer in DB 
	* 
	* @return List of all {@link SurveyUnit}
	*/
	@Query(value="SELECT pref.id_campaign "
			+ "FROM preference pref "
			+ "INNER JOIN campaign camp ON camp.id = pref.id_campaign "
			+ "INNER JOIN visibility vi ON vi.campaign_id = camp.id "
			+ "INNER JOIN user us ON us.oragnization_unit_id = vi.organization_unit_id "
			+ "WHERE pref.id_user ILIKE ?1", nativeQuery=true)
	List<String> findIdsByUserId(String idInterviewer);
	
	CampaignDto findDtoById(String id);

  /**
	* This method allows to check if user userId is associated with campaign campaignId 
	* 
	* @return List of all {@link SurveyUnit}
	*/
	@Query(value="SELECT 1 "
			+ "FROM preference pref "
			+ "WHERE pref.id_user ILIKE ?1 "
      + "AND pref.id_campaign = ?2", nativeQuery=true)
	List<Integer> checkCampaignPreferences(String userId, String campaignId);


  /**
	* This method retrieve all interviewers associated with a campaign 
	* 
	* @return List of all {@link SurveyUnit}
	*/
  @Query("SELECT "
			+ "new fr.insee.pearljam.api.dto.interviewer.InterviewerDto(interv.id, interv.firstName, interv.lastName, COUNT(su.interviewer)) "
      + "FROM SurveyUnit su "
      + "INNER JOIN Interviewer interv ON su.interviewer.id = interv.id "
      + "WHERE su.campaign.id=?1 "
      + "GROUP BY interv.id")
	List<InterviewerDto> findInterviewersDtoByCampaignId(String id);
}

