package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.message.VerifyNameResponseDto;

/**
* InterviewerRepository is the repository using to access to Interviewer table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface InterviewerRepository extends JpaRepository<Interviewer, String> {

	Optional<Interviewer> findByIdIgnoreCase(String userId);
	
	@Query(value = "SELECT id FROM interviewer WHERE "
			+ "organization_unit_id IN (:OuIds) ", nativeQuery = true)
	List<String> findIdsByOrganizationUnits(@Param("OuIds") List<String> OuIds);
	
	InterviewerDto findDtoById(String id);
	
	@Query("SELECT new fr.insee.pearljam.api.dto.interviewer.InterviewerDto(interv.id, interv.firstName, interv.lastName) "
			  + "FROM SurveyUnit su "
			  + "INNER JOIN Interviewer interv ON su.interviewer.id = interv.id "
			  + "WHERE su.id=?1 ")
	InterviewerDto findInterviewersDtoBySurveyUnitId(String id);
	
	@Query("SELECT interv "
			  + "FROM Interviewer interv "
			  + "INNER JOIN SurveyUnit su ON su.interviewer.id = interv.id "
			  + "WHERE (interv.organizationUnit.id in (:OuIds) OR 'GUEST' in (:OuIds)) "
			  + "AND su.campaign.id=:campId "
			  + "GROUP BY interv.id ")
	List<Interviewer> findInterviewersWorkingOnCampaign(@Param("campId") String campId, @Param("OuIds") List<String> OuIds);
	
	@Query("SELECT new fr.insee.pearljam.api.dto.message.VerifyNameResponseDto(interv.id,  'interviewer', concat(interv.firstName, ' ', interv.lastName)) "
			  + "FROM Interviewer interv "
			  + "WHERE interv.organizationUnit.id in (:OuIds) OR 'GUEST' in (:OuIds) "
			  + "AND (LOWER(interv.id) LIKE LOWER(concat('%',:text,'%')) "
			  + "OR LOWER(interv.firstName) LIKE LOWER(concat('%',:text,'%')) "
			  + "OR LOWER(interv.lastName) LIKE LOWER(concat('%',:text,'%'))) ")
	List<VerifyNameResponseDto> findMatchingInterviewers(@Param("text") String text, @Param("OuIds") List<String> OuIds, Pageable pageable);

}
