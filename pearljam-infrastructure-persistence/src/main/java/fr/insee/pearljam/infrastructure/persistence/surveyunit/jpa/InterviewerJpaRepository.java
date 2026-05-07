package fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa;

import fr.insee.pearljam.contracts.surveyunit.dto.interviewer.InterviewerContextDto;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
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
public interface InterviewerJpaRepository extends JpaRepository<InterviewerDB, String> {

	Optional<InterviewerDB> findByIdIgnoreCase(String userId);

	@Query(value = """
			SELECT int.id FROM interviewer int
			INNER JOIN survey_unit su ON su.interviewer_id = int.id
			WHERE su.organization_unit_id IN (:ouIds)
			AND su.campaign_id IN (:campaignIds)
			""", nativeQuery = true)
	Set<String> findIdsByOrganizationUnitsAndCampaignId(@Param("ouIds") List<String> ouIds, @Param("campaignIds") List<String> campaignIds);

	@Query("""
			SELECT new fr.insee.pearljam.contracts.surveyunit.dto.interviewer.InterviewerContextDto(interv.id, interv.firstName, interv.lastName,
			interv.email, interv.phoneNumber, interv.title)
			FROM InterviewerDB interv
			WHERE interv.id=?1""")
	InterviewerContextDto findDtoById(String id);


	@Query("SELECT interv "
			+ "FROM InterviewerDB interv "
			+ "INNER JOIN SurveyUnitDB su ON su.interviewer.id = interv.id "
			+ "WHERE (su.organizationUnit.id in (:ouIds) OR 'GUEST' in (:ouIds)) "
			+ "AND su.campaign.id=:campId "
			+ "GROUP BY interv.id ")
	List<InterviewerDB> findInterviewersWorkingOnCampaign(@Param("campId") String campId,
														  @Param("ouIds") List<String> ouIds);

	@Query("""
			    SELECT DISTINCT interv
			    FROM InterviewerDB interv
			    INNER JOIN SurveyUnitDB su
			        ON su.interviewer.id = interv.id
			    WHERE (su.organizationUnit.id IN (:ouIds) OR 'GUEST' IN (:ouIds))
				ORDER BY interv.lastName, interv.firstName
			""")
	List<InterviewerDB> findInterviewersByOrganizationUnits(@Param("ouIds") List<String> ouIds);

	@Query("select i.id from InterviewerDB i")
	List<String> findAllIds();
}
