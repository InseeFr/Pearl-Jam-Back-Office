package fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.SurveyUnit;

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
	* This method count SurveyUnits with contactOutcome = INA and states contains TBR
	* 
	* @return count of matching surveyUnits {@link Integer}
	*/
	@Query(value="SELECT COUNT(DISTINCT su.id) FROM state s " + 
			"JOIN survey_unit su ON s.survey_unit_id=su.id " + 
			"JOIN contact_outcome co ON co.survey_unit_id=su.id " + 
			"WHERE su.interviewer_id ILIKE ?1 " +
			"AND co.type='INA' " +
			"AND su.campaign_id=?2 AND s.type='TBR'", nativeQuery=true)
	Integer findCountUeINATBRByInterviewerIdAndCampaignId(String idInterviewer, String idCampaign, String idSurveyUnit);

	/**
	 * This method retrieve the SurveyUnit in DB by Id and UserId
	 * @param surveyUnitId survey unit id
	 * @param userId user id
	 * @return the survey unit
	 */
	Optional<SurveyUnit> findByIdAndInterviewerIdIgnoreCase(String surveyUnitId, String userId);

	/**
	 * This method retrieve all the Ids of the SurveyUnits in db
	 * @return List of String
	 */
	@Query(value="SELECT id "
			+ "FROM survey_unit ", nativeQuery=true)
	List<String> findAllIds();

  @Query(value = """
    SELECT su
    FROM SurveyUnit su
    WHERE su.organizationUnit.id IN (:lstOuId)
      AND EXISTS (
          SELECT vi
          FROM VisibilityDB vi
          WHERE vi.campaign.id = su.campaign.id
            AND vi.organizationUnit.id = su.organizationUnit.id
            AND vi.collectionEndDate < :date
            AND vi.endDate > :date
      )
    """)
  List<SurveyUnit> findSurveyUnitsOfOrganizationUnitsInProcessingPhase(
      @Param("date") Long date,
      @Param("lstOuId") List<String> lstOuId
  );

		@Query(value="SELECT su FROM SurveyUnit su "
		+" LEFT JOIN fetch su.comments"
		+" LEFT JOIN fetch su.states"
		+" LEFT JOIN fetch su.address"
		+" LEFT JOIN fetch su.sampleIdentifier "
		+" LEFT JOIN fetch su.interviewer "
		+" LEFT JOIN fetch su.contactOutcome "
		+" LEFT JOIN fetch su.closingCause "
		+" LEFT JOIN fetch su.identification "
		+ "WHERE su.campaign.id=:id AND su.organizationUnit.id IN (:lstOuId)")
	Set<SurveyUnit> findByCampaignIdAndOrganizationUnitIdIn(@Param("id")String id, @Param("lstOuId")List<String> lstOuId);

	List<SurveyUnit> findByInterviewerIdIgnoreCase(String id);
	
	@Query(value="SELECT "
			+ "SUM(CASE WHEN type IN ('VIC', 'PRC', 'AOC', 'APS', 'INS', 'WFT', 'WFS') THEN 1 ELSE 0 END) AS toProcessInterviewer, "
			+ "SUM(CASE WHEN type='TBR' THEN 1 ELSE 0 END) AS toBeReviewed, "
			+ "SUM(CASE WHEN type IN ('FIN', 'CLO') THEN 1 ELSE 0 END) "
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


	@Query(value="SELECT su FROM SurveyUnit su WHERE "
			+ "su.id=:id AND su.organizationUnit.id IN (:OUids)")
	List<SurveyUnit> findByIdInOrganizationalUnit(@Param("id") String id, @Param("OUids") List<String> organizationalUnitIds);

	Collection<SurveyUnit> findByCampaignId(String id);

	@Query(value="SELECT id FROM survey_unit "
			+ "WHERE campaign_id=:campaignId", nativeQuery=true)
	List<String> findAllIdsByCampaignId(@Param("campaignId") String campaignId);

	@Query(value="SELECT id FROM survey_unit "
			+ "WHERE interviewer_id=:interviewerId", nativeQuery=true)
	List<String> findAllIdsByInterviewerId(@Param("interviewerId") String interviewerId);

	@Query(value="UPDATE survey_unit "
	+ "SET interviewer_id=:interviewerId "
	+ "WHERE id IN (:surveyUnitIds)", nativeQuery=true)
	void setInterviewer(List<String> surveyUnitIds, String interviewerId);
}
