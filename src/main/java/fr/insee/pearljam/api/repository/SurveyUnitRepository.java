package fr.insee.pearljam.api.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	
	@Query(value= """
    WITH su AS (
      SELECT su1.id
          FROM survey_unit su1
      WHERE su1.campaign_id = :campaignId
        AND EXISTS (
          SELECT 1
          FROM survey_unit su2
          WHERE su2.interviewer_id = su1.interviewer_id
            AND su2.organization_unit_id in (:organizationalUnitIds)
        )
    ),
    mx AS (
      SELECT su.id AS survey_unit_id,
                 (SELECT s1.date
              FROM state s1
              WHERE s1.survey_unit_id = su.id
              ORDER BY s1.date DESC
              LIMIT 1) AS max_date
      FROM su
    )
    SELECT
      SUM((s.type IN ('VIC','PRC','AOC','APS','INS','WFT','WFS'))::int) AS toProcessInterviewer,
      SUM((s.type='TBR')::int)                                         AS toBeReviewed,
      SUM((s.type IN ('FIN','CLO'))::int)                               AS finalized,
      (SELECT COUNT(*) FROM su)                                         AS allocated
    FROM mx
    JOIN state s ON s.survey_unit_id = mx.survey_unit_id
     AND s.date = mx.max_date;
    """, nativeQuery=true)
	List<Object[]> getCampaignStats(@Param("campaignId") String campaignId, @Param("organizationalUnitIds") List<String> organizationalUnitIds);

	@Query(value="""
			SELECT su FROM SurveyUnit su
			LEFT JOIN fetch su.address
			LEFT JOIN fetch su.sampleIdentifier
			LEFT JOIN fetch su.interviewer
			LEFT JOIN fetch su.contactOutcome
			LEFT JOIN fetch su.closingCause
			LEFT JOIN fetch su.identification
			WHERE su.organizationUnit.id IN (:organisationalUnitIds)""")
	List<SurveyUnit> findByOrganizationUnitIdIn( @Param("organisationalUnitIds") List<String> lstOuId);


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
