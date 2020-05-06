package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;

/**
* SurveyUnitRepository is the repository using to access to SurveyUnit table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface SurveyUnitRepository extends JpaRepository<SurveyUnit, String> {
	/**
	* This method retrieve all SurveyUnit in DB
	* 
	* @return List of all {@link SurveyUnit}
	*/
	List<SurveyUnit> findDtoBy();
	
	/**
	* This method retrieve all Id of SurveyUnits with a certain state and idInterviewer in DB 
	* 
	* @return List of all {@link SurveyUnit}
	*/
	@Query(value="SELECT su.id as id "
			+ "FROM survey_unit su "
			+ "LEFT JOIN state st ON st.survey_unit_id = su.id "
			+ "JOIN campaign camp ON camp.id = su.campaign_id "
			+ "WHERE su.interviewer_id=?1", nativeQuery=true)
	List<String> findDtoIdBy_IdInterviewer(String idInterviewer);
	
	/**
	* This method retrieve all SurveyUnit with a certain state in DB 
	* 
	* @return List of all {@link SurveyUnit}
	*/
	@Query(value="SELECT * " 
			+ "FROM survey_unit su "
			+ "JOIN state st ON st.survey_unit_id = su.id "
			+ "WHERE st.type=?1 ", nativeQuery=true)
	List<SurveyUnit> findDtoByState(StateType state);


	/**
	 * Retrieve SurveyUnitDto with his Id
	 * @param id
	 * @return SurveyUnitDto
	 */
	SurveyUnitDto findDtoById(String id);

	void save(SurveyUnitDetailDto surveyUnitDetailDtoUpdated);

}
