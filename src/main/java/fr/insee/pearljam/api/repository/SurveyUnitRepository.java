package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
			+ "new fr.insee.pearljam.api.dto.campaign.CampaignDto(su.campaign.id, su.campaign.label) "
			+ "FROM SurveyUnit su WHERE su.id=?1")
	CampaignDto findCampaignDtoById(String id);
	
	@Query(value="SELECT DISTINCT(su.id) as id FROM survey_unit su " + 
			"INNER JOIN campaign camp on camp.id = su.campaign_id " +
			"INNER JOIN visibility vi ON vi.campaign_id = camp.id "+
			"INNER JOIN organization_unit ou ON ou.id = vi.organization_unit_id "+
			"WHERE ou.id=su.organization_unit_id AND camp.id =?1 AND ou.id ILIKE ?2", nativeQuery=true)
	List<String> findIdsByCampaignIdAndOu(String id, String ouId);
	
	
	@Query(value="SELECT su FROM SurveyUnit su " + 
			"WHERE EXISTS (SELECT vi FROM Visibility vi " +
			"WHERE vi.campaign.id = su.campaign.id " +
			"AND vi.organizationUnit.id = su.organizationUnit.id) " )
//			"AND vi.collectionEndDate < ?1 " +
//			"AND vi.endDate > ?1)")
	List<SurveyUnit> findAllSurveyUnitsInProcessingPhase(Long date);
	
	Set<SurveyUnit> findByCampaignIdAndOrganizationUnitIdIn(String id, List<String> lstOuId);
	
	@Query(value="SELECT "
			+ "SUM(CASE WHEN type IN ('VIC', 'PRC', 'AOC', 'APS', 'INS', 'WFT', 'WFS') THEN 1 ELSE 0 END) AS toProcessInterviewer, "
			+ "SUM(CASE WHEN type='TBR' THEN 1 ELSE 0 END) AS toBeReviewed, "
			+ "SUM(CASE WHEN type IN ('FIN', 'QNA') AND EXISTS (SELECT 1 FROM state s where s.type ='FIN' AND s.survey_unit_id = t.survey_unit_id) THEN 1 ELSE 0 END) "
			+ "AS finalized, "
			+ "COUNT(DISTINCT t.survey_unit_id) AS allocated "
			+ "FROM ( "
			+ "SELECT survey_unit_id, type, date FROM state WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN ("
			+ "SELECT id FROM survey_unit "
			+	"WHERE campaign_id=:campaignId "
			+	"AND interviewer_id IN (SELECT int.id FROM interviewer int INNER JOIN survey_unit su ON su.interviewer_id=int.id "
				+ "WHERE su.organization_unit_id IN (:OUids) OR 'GUEST' IN (:OUids))) "
			+ "GROUP BY survey_unit_id) "
			+ ") as t", nativeQuery=true)
	List<Object[]> getCampaignStats(@Param("campaignId") String campaignId, @Param("OUids") List<String> organizationalUnitIds);


	List<SurveyUnit> findByOrganizationUnitIdIn(List<String> lstOuId);

	@Modifying
	@Query(value="UPDATE survey_unit SET interviewer_id=NULL", nativeQuery=true)
	void updateAllinterviewersToNull();
	  
}
