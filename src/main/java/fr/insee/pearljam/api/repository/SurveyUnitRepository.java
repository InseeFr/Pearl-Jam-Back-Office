package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
			+ "LEFT JOIN state st ON st.survey_unit_id = su.id "
			+ "JOIN campaign camp ON camp.id = su.campaign_id "
			+ "WHERE su.interviewer_id ILIKE ?1", nativeQuery=true)
	List<String> findIdsByInterviewerId(String idInterviewer);

	/**
	 * This method retrieve the SurveyUnit in DB by Id and UserId
	 * @param id
	 * @param userId
	 * @return SurveyUnit
	 */
	Optional<SurveyUnit> findByIdAndInterviewerId(String id, String userId);

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

}
