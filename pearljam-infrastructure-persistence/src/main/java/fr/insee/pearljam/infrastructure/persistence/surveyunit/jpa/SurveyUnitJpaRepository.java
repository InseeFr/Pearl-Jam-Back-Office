package fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.SurveyUnitCampaignView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
* SurveyUnitRepository is the repository using to access to SurveyUnit table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface SurveyUnitJpaRepository extends JpaRepository<SurveyUnitDB, String> {

	/**
	 * This method retrieve all Id of SurveyUnits with a certain state and idInterviewer in DB for currently active campaigns
	* 
	* @return List of all {@link SurveyUnitDB}
	*/
	@Query(value = """
			SELECT su.id
			FROM survey_unit su
			
			-- pick the latest state row for this SU
			JOIN LATERAL (
			    SELECT st.type
			    FROM state st
			    WHERE st.survey_unit_id = su.id
			    ORDER BY st.date DESC
			    LIMIT 1
			) last_st ON TRUE
			
			WHERE lower(su.interviewer_id) = lower(:interviewerId)
			
			AND EXISTS (
			    SELECT 1
			    FROM (
			        SELECT DISTINCT ON (st.survey_unit_id)
			               st.survey_unit_id,
			               st.type
			        FROM state st
			        WHERE st.survey_unit_id = su.id
			        ORDER BY st.survey_unit_id, st.date DESC
			    ) last_st
			    WHERE last_st.type IN (:visibleTypes)
			)
			""", nativeQuery = true)
	List<String> findIdsByInterviewerIdWithinVisibilityScope(@Param("interviewerId") String interviewerId,
															 @Param("now") Long now,
															 @Param("visibleTypes") List<String> visibleTypes);

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
	Optional<SurveyUnitDB> findByIdAndInterviewerIdIgnoreCase(String surveyUnitId, String userId);

	/**
	 * This method retrieve all the Ids of the SurveyUnits in db
	 * @return List of String
	 */
	@Query(value="SELECT id "
			+ "FROM survey_unit ", nativeQuery=true)
	List<String> findAllIds();

	@Query(value = """
        SELECT
          su.id                              AS id,
          ls.current_state                   AS currentStateType,
          co.type                            AS contactOutcomeType
        FROM survey_unit su
        JOIN visibility vi
          ON vi.campaign_id = su.campaign_id
         AND vi.organization_unit_id = su.organization_unit_id
        JOIN LATERAL (
          SELECT s.type AS current_state
          FROM state s
          WHERE s.survey_unit_id = su.id
          ORDER BY s.date DESC
          LIMIT 1
        ) ls ON TRUE
        LEFT JOIN contact_outcome co
          ON co.survey_unit_id = su.id
        WHERE su.organization_unit_id IN (:lstOuIds)
        AND vi.collection_end_date < :date
        AND vi.end_date > :date
        AND (
          ls.current_state NOT IN ('TBR','FIN','CLO')
          OR co.type = 'INA'
        )
        """, nativeQuery = true)
	List<ClosableSurveyUnitCandidateView> findClosableCandidates(
			@Param("date") long date,
			@Param("lstOuIds") List<String> lstOuIds
	);

	@Query(value = """
        SELECT
          su.id                              AS id,
          su.display_name                    AS displayName,
          si.ssech                           AS ssech,
           a.l6                              AS addressL6,
          ls.current_state                   AS currentStateType,
          cc.type                            AS closingCauseType,
          co.type                            AS contactOutcomeType,
         int.id                              AS interviewerId,
         int.first_name                      AS interviewerFirstName,
         int.last_name                       AS interviewerLastName
        FROM survey_unit su
        JOIN LATERAL (
          SELECT s.type AS current_state
          FROM state s
          WHERE s.survey_unit_id = su.id
          ORDER BY s.date DESC
          LIMIT 1
        ) ls ON TRUE
        LEFT JOIN address a
            ON a.id = su.address_id
        LEFT JOIN sample_identifier si
            ON si.id = su.sample_identifier_id
        LEFT JOIN closing_cause cc
            ON cc.survey_unit_id = su.id
        LEFT JOIN contact_outcome co
            ON co.survey_unit_id = su.id
        LEFT JOIN interviewer int
            ON int.id = su.interviewer_id
        WHERE su.organization_unit_id IN (:lstOuIds)
        AND su.campaign_id = :campaignId
    """, nativeQuery = true)
	Set<SurveyUnitCampaignView> findByCampaignIdAndOrganizationUnitIdIn(
			@Param("campaignId") String campaignId,
			@Param("lstOuIds") List<String> lstOuId);

	@Query(value = """
	    WITH su_scope AS (
	        SELECT
	            su.id,
	            su.display_name,
	            su.viewed,
	            su.address_id,
	            su.sample_identifier_id,
	            su.interviewer_id
	        FROM survey_unit su
	        WHERE su.campaign_id = :campaignId
	        AND su.organization_unit_id IN (:lstOuIds)
	        AND EXISTS (
	            SELECT 1
	            FROM state s
	            WHERE s.survey_unit_id = su.id
	            AND s.type = :state
	        )
	    )
	    SELECT
	        su.id                              AS id,
	        su.display_name                    AS displayName,
	        su.viewed                          AS viewed,
	        si.ssech                           AS ssech,
	        a.l6                               AS addressL6,
	        cc.type                            AS closingCauseType,
	        co.type                            AS contactOutcomeType,
	        int.id                             AS interviewerId,
	        int.first_name                     AS interviewerFirstName,
	        int.last_name                      AS interviewerLastName,
	        com.type                           AS commentType,
	        com.value                          AS commentValue,
	        ls.current_state                   AS currentStateType
	    FROM su_scope su
	    JOIN LATERAL (
	        SELECT s.type AS current_state
	        FROM state s
	        WHERE s.survey_unit_id = su.id
	        ORDER BY s.date DESC
	        LIMIT 1
	    ) ls ON ls.current_state = :state
	    LEFT JOIN address a
	        ON a.id = su.address_id
	    LEFT JOIN sample_identifier si
	        ON si.id = su.sample_identifier_id
	    LEFT JOIN closing_cause cc
	        ON cc.survey_unit_id = su.id
	    LEFT JOIN contact_outcome co
	        ON co.survey_unit_id = su.id
	    LEFT JOIN interviewer int
	        ON int.id = su.interviewer_id
	    LEFT JOIN LATERAL (
	        SELECT c.type, c.value
	        FROM comment c
	        WHERE c.survey_unit_id = su.id
	        AND c.type = 'MANAGEMENT'
	        LIMIT 1
	    ) com ON TRUE;
	""", nativeQuery = true)
	Set<SurveyUnitCampaignView> findByCampaignIdAndStateAndOrganizationUnitIdIn(@Param("campaignId") String campaignId,
																					  @Param("lstOuIds") List<String> lstOuId,
																					  @Param("state") String state);

	@Query(value = """
	    WITH su_scope AS (
	        SELECT
	            su.id,
	            su.display_name,
	            su.address_id,
	            su.sample_identifier_id,
	            su.interviewer_id
	        FROM survey_unit su
	        WHERE su.campaign_id = :campaignId
	        AND su.organization_unit_id IN (:lstOuIds)
	        AND EXISTS (
	            SELECT 1
	            FROM state s
	            WHERE s.survey_unit_id = su.id
	            AND s.type = 'FIN'
	        )
	    )
	    SELECT
	        su.id                              AS id,
	        su.display_name                    AS displayName,
	        si.ssech                           AS ssech,
	        a.l6                               AS addressL6,
	        cc.type                            AS closingCauseType,
	        co.type                            AS contactOutcomeType,
	        int.id                             AS interviewerId,
	        int.first_name                     AS interviewerFirstName,
	        int.last_name                      AS interviewerLastName,
	        f.finalizationDate                 AS finalizationDate,
	        com.type                           AS commentType,
	        com.value                          AS commentValue,
	        (tbr.exists_tbr IS NOT NULL)       AS reading
	    FROM su_scope su
	    JOIN LATERAL (
	        SELECT s.date AS finalizationDate
	        FROM state s
	        WHERE s.survey_unit_id = su.id
	        AND s.type IN ('FIN','CLO')
	        ORDER BY s.date DESC
	        LIMIT 1
	    ) f ON TRUE
	    LEFT JOIN LATERAL (
	        SELECT 1 AS exists_tbr
	        FROM state s_tbr
	        WHERE s_tbr.survey_unit_id = su.id
	        AND s_tbr.type = 'TBR'
	        LIMIT 1
	    ) tbr ON TRUE
	    LEFT JOIN address a
	        ON a.id = su.address_id
	    LEFT JOIN sample_identifier si
	        ON si.id = su.sample_identifier_id
	    LEFT JOIN closing_cause cc
	        ON cc.survey_unit_id = su.id
	    LEFT JOIN contact_outcome co
	        ON co.survey_unit_id = su.id
	    LEFT JOIN interviewer int
	        ON int.id = su.interviewer_id
	    LEFT JOIN LATERAL (
	        SELECT c.type, c.value
	        FROM comment c
	        WHERE c.survey_unit_id = su.id
	        AND c.type = 'MANAGEMENT'
	        LIMIT 1
	    ) com ON TRUE;
	""", nativeQuery = true)
	Set<SurveyUnitCampaignView> findFinalizedByCampaignIdAndOrganizationUnitIdIn(@Param("campaignId") String campaignId,
																					   @Param("lstOuIds") List<String> lstOuId);

	@Query(value = """
	    WITH su_scope AS (
	        SELECT
	            su.id,
	            su.display_name,
	            su.address_id,
	            su.sample_identifier_id,
	            su.interviewer_id
	        FROM survey_unit su
	        WHERE su.campaign_id = :campaignId
	        AND su.organization_unit_id IN (:lstOuIds)
	        AND EXISTS (
	            SELECT 1
	            FROM state s
	            WHERE s.survey_unit_id = su.id
	            AND s.type = 'CLO'
	        )
	    )
	    SELECT
	        su.id                              AS id,
	        su.display_name                    AS displayName,
	        si.ssech                           AS ssech,
	        a.l6                               AS addressL6,
	        cc.type                            AS closingCauseType,
	        co.type                            AS contactOutcomeType,
	        int.id                             AS interviewerId,
	        int.first_name                     AS interviewerFirstName,
	        int.last_name                      AS interviewerLastName,
	        f.finalizationDate                 AS finalizationDate,
	        com.type                           AS commentType,
	        com.value                          AS commentValue,
	        (tbr.exists_tbr IS NOT NULL)       AS reading
	    FROM su_scope su
	    JOIN LATERAL (
	        SELECT s.type AS current_state
	        FROM state s
	        WHERE s.survey_unit_id = su.id
	        ORDER BY s.date DESC
	        LIMIT 1
	    ) ls ON ls.current_state = 'CLO'
	    JOIN LATERAL (
	        SELECT s.date AS finalizationDate
	        FROM state s
	        WHERE s.survey_unit_id = su.id
	        AND s.type IN ('FIN','CLO')
	        ORDER BY s.date DESC
	        LIMIT 1
	    ) f ON TRUE
	    LEFT JOIN LATERAL (
	        SELECT 1 AS exists_tbr
	        FROM state s_tbr
	        WHERE s_tbr.survey_unit_id = su.id
	        AND s_tbr.type = 'TBR'
	        LIMIT 1
	    ) tbr ON TRUE
	    LEFT JOIN address a
	        ON a.id = su.address_id
	    LEFT JOIN sample_identifier si
	        ON si.id = su.sample_identifier_id
	    LEFT JOIN closing_cause cc
	        ON cc.survey_unit_id = su.id
	    LEFT JOIN contact_outcome co
	        ON co.survey_unit_id = su.id
	    LEFT JOIN interviewer int
	        ON int.id = su.interviewer_id
	    LEFT JOIN LATERAL (
	        SELECT c.type, c.value
	        FROM comment c
	        WHERE c.survey_unit_id = su.id
	        AND c.type = 'MANAGEMENT'
	        LIMIT 1
	    ) com ON TRUE;
	""", nativeQuery = true)
	Set<SurveyUnitCampaignView> findClosedByCampaignIdAndOrganizationUnitIdIn(@Param("campaignId") String campaignId,
																					   @Param("lstOuIds") List<String> lstOuId);

	List<SurveyUnitDB> findByInterviewerIdIgnoreCase(String id);
	
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
			SELECT su FROM SurveyUnitDB su
			LEFT JOIN fetch su.address
			LEFT JOIN fetch su.sampleIdentifier
			LEFT JOIN fetch su.interviewer
			LEFT JOIN fetch su.contactOutcome
			LEFT JOIN fetch su.closingCause
			LEFT JOIN fetch su.identification
			WHERE su.organizationUnit.id IN (:organisationalUnitIds)""")
	List<SurveyUnitDB> findByOrganizationUnitIdIn(@Param("organisationalUnitIds") List<String> lstOuId);


	@Query(value="SELECT su FROM SurveyUnitDB su WHERE "
			+ "su.id=:id AND su.organizationUnit.id IN (:OUids)")
	List<SurveyUnitDB> findByIdInOrganizationalUnit(@Param("id") String id, @Param("OUids") List<String> organizationalUnitIds);

	Collection<SurveyUnitDB> findByCampaignId(String id);

	@Query(value="SELECT id FROM survey_unit "
			+ "WHERE campaign_id=:campaignId", nativeQuery=true)
	List<String> findAllIdsByCampaignId(@Param("campaignId") String campaignId);

	@Query(value="SELECT id FROM survey_unit "
			+ "WHERE interviewer_id=:interviewerId", nativeQuery=true)
	List<String> findAllIdsByInterviewerId(@Param("interviewerId") String interviewerId);

	@Query(value="SELECT COUNT(*) FROM survey_unit "
			+ "WHERE interviewer_id IS NULL AND campaign_id=:campaignId", nativeQuery=true)
	Integer countUnallocatedSurveyUnitsByCampaignId(@Param("campaignId") String campaignId);

	@Query(value="SELECT COUNT(*) FROM survey_unit "
			+ "WHERE interviewer_id IS NULL AND campaign_id=:campaignId AND organization_unit_id IN (:organizationUnitIds)", nativeQuery=true)
	Integer countUnallocatedSurveyUnitsByCampaignIdAndOrganizationUnitIdIn(@Param("campaignId") String campaignId, @Param("organizationUnitIds") List<String> organizationUnitIds);

	@Query(value="UPDATE survey_unit "
	+ "SET interviewer_id=:interviewerId "
	+ "WHERE id IN (:surveyUnitIds)", nativeQuery=true)
	void setInterviewer(List<String> surveyUnitIds, String interviewerId);

	@Query(value = """
    WITH finalization AS (
      SELECT
        s2.survey_unit_id,
        MAX(s2.date) AS finalizationDate
      FROM state s2
      WHERE s2.type IN ('FIN','CLO')
      GROUP BY s2.survey_unit_id
    )
    SELECT
    su.id                    AS id,
    su.display_name          AS displayName,
    si.ssech                 AS ssech,
    a.l6                     AS addressL6,
    c.label                  AS campaignLabel,
    c.identification_configuration AS campaignIdentificationConfiguration,
    cc.type                        AS closingCauseType,
    idf.identification_type        AS identificationType,
    idf.identification             AS identification,
    idf.access                     AS access,
    idf.situation                  AS situation,
    idf.category                   AS category,
    idf.occupant                   AS occupant,
    idf.individual_status          AS individualStatus,
    idf.interviewer_can_process    AS interviewerCanProcess,
    idf.number_of_respondents      AS numberOfRespondents,
    idf.present_in_previous_home   AS presentInPreviousHome,
    idf.household_composition      AS householdComposition,
    int.first_name                 AS interviewerFirstName,
    int.last_name                  AS interviewerLastName,
    f.finalizationDate              AS finalizationDate
    FROM survey_unit su
    LEFT JOIN campaign c ON c.id = su.campaign_id
    LEFT JOIN address a ON a.id = su.address_id
    LEFT JOIN sample_identifier si ON si.id = su.sample_identifier_id
    LEFT JOIN closing_cause cc ON cc.survey_unit_id = su.id
    LEFT JOIN identification idf ON idf.survey_unit_id = su.id
    LEFT JOIN interviewer int ON int.id = su.interviewer_id
    LEFT JOIN finalization f ON f.survey_unit_id = su.id
    WHERE su.id IN (:ids)
    """, nativeQuery = true)
	List<ClosableSurveyUnitView> findClosableSurveyUnits(@Param("ids") Set<String> ids);

	@Query(value = "SELECT id FROM survey_unit WHERE id IN (:surveyUnitIds)",
			nativeQuery = true)
	List<String> findExistingIds(@Param("surveyUnitIds") List<String> surveyUnitIds);

	List<SurveyUnitFetchedByStatesAndCampaignIdView> getSurveyUnitsByStatesAndCampaignId(
			@Param("stateTypes") List<String> stateTypes,
			@Param("campaignId") String campaignId,
			String search,
			Pageable pageable);

	/**
	 * Retrieves IDs of all survey units eligible for closing.
	 * Used for pagination in getSurveyUnitsToClose.
	 *
	 * @param date current timestamp
	 * @param lstOuIds list of Organization Unit IDs for the user
	 * @return list of eligible survey unit IDs
	 */
	@Query(value = """
	    SELECT su.id
	    FROM survey_unit su
	    JOIN visibility vi 
	        ON vi.campaign_id = su.campaign_id 
	        AND vi.organization_unit_id = su.organization_unit_id
	    JOIN LATERAL (
	        SELECT s.type AS current_state
	        FROM state s
	        WHERE s.survey_unit_id = su.id
	        ORDER BY s.date DESC
	        LIMIT 1
	    ) ls ON TRUE
	    LEFT JOIN contact_outcome co ON co.survey_unit_id = su.id
	    WHERE su.organization_unit_id IN (:lstOuIds)
	    AND vi.collection_end_date < :date
	    AND vi.end_date > :date
	    AND (
	        ls.current_state NOT IN ('TBR','FIN','CLO')
	        OR (co.type = 'INA' AND ls.current_state != 'CLO')
	    )
	    """, nativeQuery = true)
	List<String> findEligibleSurveyUnitIds(
		@Param("date") long date,
		@Param("lstOuIds") List<String> lstOuIds
	);

	/**
	 * Counts the total number of survey units eligible for closing.
	 * Used for pagination in getSurveyUnitsToClose.
	 *
	 * @param date current timestamp
	 * @param lstOuIds list of Organization Unit IDs for the user
	 * @return total count of eligible survey units
	 */
	@Query(value = """
	    SELECT COUNT(DISTINCT su.id)
	    FROM survey_unit su
	    JOIN visibility vi 
	        ON vi.campaign_id = su.campaign_id 
	        AND vi.organization_unit_id = su.organization_unit_id
	    JOIN LATERAL (
	        SELECT s.type AS current_state
	        FROM state s
	        WHERE s.survey_unit_id = su.id
	        ORDER BY s.date DESC
	        LIMIT 1
	    ) ls ON TRUE
	    LEFT JOIN contact_outcome co ON co.survey_unit_id = su.id
	    WHERE su.organization_unit_id IN (:lstOuIds)
	    AND vi.collection_end_date < :date
	    AND vi.end_date > :date
	    AND (
	        ls.current_state NOT IN ('TBR','FIN','CLO')
	        OR (co.type = 'INA' AND ls.current_state != 'CLO')
	    )
	    """, nativeQuery = true)
	long countEligibleSurveyUnits(
		@Param("date") long date,
		@Param("lstOuIds") List<String> lstOuIds
	);

	/**
	 * Retrieves ClosableSurveyUnitCandidateView for a specific list of IDs.
	 * Used for pagination in getSurveyUnitsToClose.
	 *
	 * @param ids list of survey unit IDs
	 * @param date current timestamp (kept for interface consistency, not used in query)
	 * @return list of matching candidates
	 */
	@Query(value = """
	    SELECT
	        su.id AS id,
	        ls.current_state AS currentStateType,
	        co.type AS contactOutcomeType
	    FROM survey_unit su
	    JOIN LATERAL (
	        SELECT s.type AS current_state
	        FROM state s
	        WHERE s.survey_unit_id = su.id
	        ORDER BY s.date DESC
	        LIMIT 1
	    ) ls ON TRUE
	    LEFT JOIN contact_outcome co ON co.survey_unit_id = su.id
	    WHERE su.id IN (:ids)
	    """, nativeQuery = true)
	List<ClosableSurveyUnitCandidateView> findClosableCandidatesByIds(
		@Param("ids") List<String> ids,
		@Param("date") long date
	);
}
