package fr.insee.pearljam.infrastructure.interviewer.jpa;

import fr.insee.pearljam.domain.interviewer.model.Interviewer;
import fr.insee.pearljam.api.interviewer.dto.InterviewerContextDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * InterviewerRepository is the repository using to access to Interviewer table
 * in DB
 * 
 * @author Claudel Benjamin
 * 
 */
public interface InterviewerJpaRepository extends JpaRepository<Interviewer, String> {

	Optional<Interviewer> findByIdIgnoreCase(String userId);

	@Query(value = """
			SELECT int.id FROM interviewer int
			INNER JOIN survey_unit su ON su.interviewer_id = int.id
			WHERE su.organization_unit_id IN (:ouIds)
			AND su.campaign_id IN (:campaignIds)
			""", nativeQuery = true)
	Set<String> findIdsByOrganizationUnitsAndCampaignId(@Param("ouIds") List<String> ouIds, @Param("campaignIds") List<String> campaignIds);

	@Query("""
			SELECT new fr.insee.pearljam.api.interviewer.dto.InterviewerContextDto(interv.id, interv.firstName, interv.lastName,
			interv.email, interv.phoneNumber, interv.title)
			FROM Interviewer interv
			WHERE interv.id=?1""")
	InterviewerContextDto findDtoById(String id);


	@Query("SELECT interv "
			+ "FROM Interviewer interv "
			+ "INNER JOIN SurveyUnit su ON su.interviewer.id = interv.id "
			+ "WHERE (su.organizationUnit.id in (:ouIds) OR 'GUEST' in (:ouIds)) "
			+ "AND su.campaign.id=:campId "
			+ "GROUP BY interv.id ")
	List<Interviewer> findInterviewersWorkingOnCampaign(@Param("campId") String campId,
			@Param("ouIds") List<String> ouIds);

	@Query("""
			    SELECT DISTINCT interv
			    FROM Interviewer interv
			    INNER JOIN SurveyUnit su
			        ON su.interviewer.id = interv.id
			    WHERE (su.organizationUnit.id IN (:ouIds) OR 'GUEST' IN (:ouIds))
			""")
	List<Interviewer> findInterviewersByOrganizationUnits(@Param("ouIds") List<String> ouIds);

	@Query("select p.id from #{#entityName} p")
	List<String> findAllIds();
}
