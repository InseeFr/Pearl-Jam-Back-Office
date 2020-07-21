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
			+ "INNER JOIN public.user us ON us.organization_unit_id = vi.organization_unit_id "
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
	* This method retrieves all interviewers associated with a campaign 
	* 
	* @return List of all {@link SurveyUnit}
	*/
  @Query("SELECT "
			+ "new fr.insee.pearljam.api.dto.interviewer.InterviewerDto(interv.id, interv.firstName, interv.lastName, COUNT(su.interviewer)) "
      + "FROM SurveyUnit su "
      + "INNER JOIN Interviewer interv ON su.interviewer.id = interv.id "
      + "WHERE su.campaign.id=?1 "
      + "AND (interv.organizationUnit.id=?2 OR ?2='GUEST') "
      + "GROUP BY interv.id")
	List<InterviewerDto> findInterviewersDtoByCampaignId(String id, String organizationUnit);

  /**
	* This method retrieves the counts of survey units by state for an interviewer and a campaign 
	* 
	* @return {@link List<Object[]>}
	*/
  @Query(value="SELECT "
      + "SUM(CASE WHEN type='ANS' THEN 1 ELSE 0 END) AS ansCount, "
		  + "SUM(CASE WHEN type='PRC' THEN 1 ELSE 0 END) AS prcCount, "
      + "SUM(CASE WHEN type='AOC' THEN 1 ELSE 0 END) AS aocCount, "
      + "SUM(CASE WHEN type='APS' THEN 1 ELSE 0 END) AS apsCount, "
      + "SUM(CASE WHEN type='INS' THEN 1 ELSE 0 END) AS insCount, "
      + "SUM(CASE WHEN type='WFT' THEN 1 ELSE 0 END) AS wftCount, "
      + "SUM(CASE WHEN type='WFS' THEN 1 ELSE 0 END) AS wfsCount, "
      + "SUM(CASE WHEN type='TBR' THEN 1 ELSE 0 END) AS tbrCount, "
      + "SUM(CASE WHEN type='FIN' THEN 1 ELSE 0 END) AS finCount, "
      + "SUM(CASE WHEN type='NVI' THEN 1 ELSE 0 END) AS nviCount, "
      + "SUM(CASE WHEN type='NVM' THEN 1 ELSE 0 END) AS nvmCount, "
      + "COUNT(1) AS total "
      + "FROM ( "
	    + "SELECT survey_unit_id, type, date FROM state WHERE (survey_unit_id, date) IN ("
	    + "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN ("
      + "SELECT id FROM survey_unit "
			+	"WHERE campaign_id=?1 "
			+	"AND interviewer_id=?2 ) "
		  + "AND (date<=?3 OR ?3<0) GROUP BY survey_unit_id) "
      + ") as t", nativeQuery=true)
  List<Object[]> getStateCount(String campaignId, String interviewerId, Long date);
  
  @Query(value="SELECT "
	      + "SUM(CASE WHEN type='ANS' THEN 1 ELSE 0 END) AS ansCount, "
	      + "SUM(CASE WHEN type='PRC' THEN 1 ELSE 0 END) AS prcCount, "
	      + "SUM(CASE WHEN type='AOC' THEN 1 ELSE 0 END) AS aocCount, "
	      + "SUM(CASE WHEN type='APS' THEN 1 ELSE 0 END) AS apsCount, "
	      + "SUM(CASE WHEN type='INS' THEN 1 ELSE 0 END) AS insCount, "
	      + "SUM(CASE WHEN type='WFT' THEN 1 ELSE 0 END) AS wftCount, "
	      + "SUM(CASE WHEN type='WFS' THEN 1 ELSE 0 END) AS wfsCount, "
	      + "SUM(CASE WHEN type='TBR' THEN 1 ELSE 0 END) AS tbrCount, "
	      + "SUM(CASE WHEN type='FIN' THEN 1 ELSE 0 END) AS finCount, "
	      + "SUM(CASE WHEN type='NVI' THEN 1 ELSE 0 END) AS nviCount, "
	      + "SUM(CASE WHEN type='NVM' THEN 1 ELSE 0 END) AS nvmCount, "
	      + "COUNT(1) AS total "
	      + "FROM ( "
		    + "SELECT survey_unit_id, type, date FROM state WHERE (survey_unit_id, date) IN ("
		    + "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN ("
	      + "SELECT id FROM survey_unit "
				+	"WHERE campaign_id=?1 "
				+	"AND interviewer_id IN (SELECT int.id FROM interviewer int WHERE int.organization_unit_id =?2 )) "
			  + "AND (date<=?3 OR ?3<0) GROUP BY survey_unit_id) "
	      + ") as t", nativeQuery=true)
  List<Object[]> getStateCountByCampaignAndOU(String campaignId, String organizationalUnitId, Long date);
  
  @Query(value="SELECT "
	      + "SUM(CASE WHEN type='ANS' THEN 1 ELSE 0 END) AS ansCount, "
	      + "SUM(CASE WHEN type='PRC' THEN 1 ELSE 0 END) AS prcCount, "
	      + "SUM(CASE WHEN type='AOC' THEN 1 ELSE 0 END) AS aocCount, "
	      + "SUM(CASE WHEN type='APS' THEN 1 ELSE 0 END) AS apsCount, "
	      + "SUM(CASE WHEN type='INS' THEN 1 ELSE 0 END) AS insCount, "
	      + "SUM(CASE WHEN type='WFT' THEN 1 ELSE 0 END) AS wftCount, "
	      + "SUM(CASE WHEN type='WFS' THEN 1 ELSE 0 END) AS wfsCount, "
	      + "SUM(CASE WHEN type='TBR' THEN 1 ELSE 0 END) AS tbrCount, "
	      + "SUM(CASE WHEN type='FIN' THEN 1 ELSE 0 END) AS finCount, "
	      + "SUM(CASE WHEN type='NVI' THEN 1 ELSE 0 END) AS nviCount, "
	      + "SUM(CASE WHEN type='NVM' THEN 1 ELSE 0 END) AS nvmCount, "
	      + "COUNT(1) AS total "
	      + "FROM ( "
		    + "SELECT survey_unit_id, type, date FROM state WHERE (survey_unit_id, date) IN ("
		    + "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN ("
	      + "SELECT id FROM survey_unit "
				+	"WHERE campaign_id=?1) "
			  + "AND (date<=?2 OR ?2<0) GROUP BY survey_unit_id) "
	      + ") as t", nativeQuery=true)
  List<Object[]> getStateCountByCampaignId(String campaignId, Long date);
}   
