package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.SurveyUnit;

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
			+ "WHERE su.interviewer_id=?1", nativeQuery=true)
	List<String> findDtoIdBy_IdInterviewer(String idInterviewer);

	
	Optional<SurveyUnit> findByIdAndInterviewerId(String id, String userId);

	@Query(value="SELECT id "
			+ "FROM survey_unit ", nativeQuery=true)
	List<String> findAllIds();

}
