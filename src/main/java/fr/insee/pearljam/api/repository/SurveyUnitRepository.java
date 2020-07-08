package fr.insee.pearljam.api.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.projection.SurveyUnitCampaign;

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

	@Query(value="SELECT su.id, ad.l6, si.ssech FROM survey_unit su " + 
			"INNER JOIN address ad on ad.id = su.address_id " +
			"INNER JOIN sample_identifier si ON si.id = su.sample_identifier_id " + 
			"INNER JOIN campaign camp on camp.id = su.campaign_id " +
			"INNER JOIN preference pref on pref.id_campaign = camp.id " +
			"WHERE camp.id =?1 AND pref.id_user ILIKE ?2", nativeQuery=true)
	Collection<SurveyUnitCampaign> getSurveyUnitByCampaignId(String id, String userId);

}
