package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;

/**
* SurveyUnitRepository is the repository using to access to SurveyUnit table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface SurveyUnitRepository extends JpaRepository<SurveyUnit, String> {

	/**
	* This method retrieve all Id of SurveyUnits with a certain state and idInterviewer in DB 
	* 
	* @return List of all {@link SurveyUnit}
	*/
	@Query(value="SELECT su.id as id "
			+ "FROM survey_unit su "
			+ "JOIN campaign camp ON camp.id = su.campaign_id "
			+ "WHERE su.interviewer_id ILIKE ?1", nativeQuery=true)
	List<String> findIdsByInterviewerId(String idInterviewer);
	
	/**
	* This method retrieve all Id of SurveyUnits with a certain state and idInterviewer in DB 
	* 
	* @return List of all {@link SurveyUnit}
	*/
	@Query(value="SELECT COUNT(DISTINCT su.id) FROM state s " + 
			"JOIN survey_unit su ON s.survey_unit_id=su.id " + 
			"WHERE su.interviewer_id ILIKE ?1 " +
			"AND su.campaign_id=?2 AND s.type='TBR'", nativeQuery=true)
	Integer findCountUeTBRByInterviewerIdAndCampaignId(String idInterviewer, String idCampaign, String idSurveyUnit);

	/**
	 * This method retrieve the SurveyUnit in DB by Id and UserId
	 * @param id
	 * @param userId
	 * @return SurveyUnit
	 */
	Optional<SurveyUnit> findByIdAndInterviewerIdIgnoreCase(String id, String userId);

	/**
	 * This method retrieve all the Ids of the SurveyUnits in db
	 * @return List of String
	 */
	@Query(value="SELECT id "
			+ "FROM survey_unit ", nativeQuery=true)
	List<String> findAllIds();
	
	
	@Query("SELECT "
			+ "new fr.insee.pearljam.api.dto.campaign.CampaignDto(su.campaign.id, su.campaign.label,su.campaign.collectionStartDate,su.campaign.collectionEndDate) "
			+ "FROM SurveyUnit su WHERE su.id=?1")
	CampaignDto findCampaignDtoById(String id);
	
	@Query(value="SELECT su.id as id FROM survey_unit su " + 
			"INNER JOIN campaign camp on camp.id = su.campaign_id " +
			"INNER JOIN visibility vi ON vi.campaign_id = camp.id "+
			"INNER JOIN organization_unit ou ON ou.id = vi.organization_unit_id "+
			"INNER JOIN interviewer int on int.id = su.interviewer_id AND int.organization_unit_id = ou.id " +
			"WHERE camp.id =?1 AND ou.id ILIKE ?2 ", nativeQuery=true)
	List<String> findIdsByCampaignIdAndOu(String id, String ouId);

	@Query(value="SELECT su.id as id FROM survey_unit su " + 
			"INNER JOIN campaign camp on camp.id = su.campaign_id " +
			"INNER JOIN visibility vi ON vi.campaign_id = camp.id "+
			"INNER JOIN organization_unit ou ON ou.id = vi.organization_unit_id "+
			"INNER JOIN interviewer int on int.id = su.interviewer_id " +
			"INNER JOIN state st on st.survey_unit_id = su.id AND int.organization_unit_id = ou.id "+
			"WHERE camp.id =?1 AND st.type = ?2 "+ 
			"AND st.date = (SELECT MAX(date) FROM state WHERE survey_unit_id=su.id) "+ 
			"AND ou.id ILIKE ?3 ", nativeQuery=true)
	List<String> findIdsByCampaignIdAndStateAndOu(String id, String state, String ouId);
	
	
	@Query(value="SELECT "
			+ "SUM(CASE WHEN type IN ('VIC', 'PRC', 'AOC', 'APS', 'INS', 'WFT', 'WFS') THEN 1 ELSE 0 END) AS toProcessInterviewer, "
			+ "SUM(CASE WHEN type='TBR' THEN 1 ELSE 0 END) AS toBeReviewed, "
			+ "SUM(CASE WHEN type='FIN' THEN 1 ELSE 0 END) AS finalized, "
			+ "COUNT(1) AS allocated "
			+ "FROM ( "
			+ "SELECT survey_unit_id, type, date FROM state WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN ("
			+ "SELECT id FROM survey_unit "
			+	"WHERE campaign_id=:campaignId "
			+	"AND interviewer_id IN (SELECT int.id FROM interviewer int WHERE int.organization_unit_id IN (:OUids) OR 'GUEST' IN (:OUids))) "
			+ "GROUP BY survey_unit_id) "
			+ ") as t", nativeQuery=true)
	  List<Object[]> getCampaignStats(@Param("campaignId") String campaignId, @Param("OUids") List<String> organizationalUnitIds);
}
