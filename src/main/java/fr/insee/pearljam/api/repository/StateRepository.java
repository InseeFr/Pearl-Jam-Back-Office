package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.insee.pearljam.domain.count.model.StateCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.state.StateDto;

/**
 * StateRepository is the repository using to access to State table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
public interface StateRepository extends JpaRepository<State, Long> {
	/**
	 * This method retrieve the State in db the SurveyUnit associated
	 * 
	 * @param surveyUnit su
	 * @return StateDto
	 */
	StateDto findFirstDtoBySurveyUnitOrderByDateDesc(SurveyUnit surveyUnit);

	List<StateDto> findAllDtoBySurveyUnitIdOrderByDateAsc(String suId);

	Optional<StateDto> findDtoById(Long long1);

	/**
	 * This method retrieve the last State in db the SurveyUnitId associated
	 * 
	 * @param surveyUnitId id
	 * @return State type
	 */
	StateDto findFirstDtoBySurveyUnitIdOrderByDateDesc(String surveyUnitId);

	@Query(value = "SELECT "
			+ "SUM(CASE WHEN type='NVM' THEN 1 ELSE 0 END) AS nvmCount, "
			+ "SUM(CASE WHEN type='NNS' THEN 1 ELSE 0 END) AS nnsCount, "
			+ "SUM(CASE WHEN type='ANV' THEN 1 ELSE 0 END) AS anvCount, "
			+ "SUM(CASE WHEN type='VIN' THEN 1 ELSE 0 END) AS vinCount, "
			+ "SUM(CASE WHEN type='VIC' THEN 1 ELSE 0 END) AS vicCount, "
			+ "SUM(CASE WHEN type='PRC' THEN 1 ELSE 0 END) AS prcCount, "
			+ "SUM(CASE WHEN type='AOC' THEN 1 ELSE 0 END) AS aocCount, "
			+ "SUM(CASE WHEN type='APS' THEN 1 ELSE 0 END) AS apsCount, "
			+ "SUM(CASE WHEN type='INS' THEN 1 ELSE 0 END) AS insCount, "
			+ "SUM(CASE WHEN type='WFT' THEN 1 ELSE 0 END) AS wftCount, "
			+ "SUM(CASE WHEN type='WFS' THEN 1 ELSE 0 END) AS wfsCount, "
			+ "SUM(CASE WHEN type='TBR' THEN 1 ELSE 0 END) AS tbrCount, "
			+ "SUM(CASE WHEN type='FIN' THEN 1 ELSE 0 END) AS finCount, "
			+ "SUM(CASE WHEN type='CLO' THEN 1 ELSE 0 END) AS cloCount, "
			+ "SUM(CASE WHEN type='NVA' THEN 1 ELSE 0 END) AS nvaCount, "
			+ "COUNT(1) AS total "
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date "
			+ "FROM state "
			+ "WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN ("
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id IN (:ouIds) "
			+ "AND interviewer_id=:interviewerId "
			+ ") "
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") "
			+ ") as t", nativeQuery = true)
	Map<String, Long> getStateCount(@Param("campaignId") String campaignId,
			@Param("interviewerId") String interviewerId, @Param("ouIds") List<String> ouIds,
			@Param("date") Long date);

	@Query(value = "SELECT "
			+ "SUM(CASE WHEN type='NVM' THEN 1 ELSE 0 END) AS nvmCount, "
			+ "SUM(CASE WHEN type='NNS' THEN 1 ELSE 0 END) AS nnsCount, "
			+ "SUM(CASE WHEN type='ANV' THEN 1 ELSE 0 END) AS anvCount, "
			+ "SUM(CASE WHEN type='VIN' THEN 1 ELSE 0 END) AS vinCount, "
			+ "SUM(CASE WHEN type='VIC' THEN 1 ELSE 0 END) AS vicCount, "
			+ "SUM(CASE WHEN type='PRC' THEN 1 ELSE 0 END) AS prcCount, "
			+ "SUM(CASE WHEN type='AOC' THEN 1 ELSE 0 END) AS aocCount, "
			+ "SUM(CASE WHEN type='APS' THEN 1 ELSE 0 END) AS apsCount, "
			+ "SUM(CASE WHEN type='INS' THEN 1 ELSE 0 END) AS insCount, "
			+ "SUM(CASE WHEN type='WFT' THEN 1 ELSE 0 END) AS wftCount, "
			+ "SUM(CASE WHEN type='WFS' THEN 1 ELSE 0 END) AS wfsCount, "
			+ "SUM(CASE WHEN type='TBR' THEN 1 ELSE 0 END) AS tbrCount, "
			+ "SUM(CASE WHEN type='FIN' THEN 1 ELSE 0 END) AS finCount, "
			+ "SUM(CASE WHEN type='CLO' THEN 1 ELSE 0 END) AS cloCount, "
			+ "SUM(CASE WHEN type='NVA' THEN 1 ELSE 0 END) AS nvaCount, "
			+ "COUNT(1) AS total "
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM state WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN ("
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id IN (:campaignIds) "
			+ "AND organization_unit_id IN (:ouIds) "
			+ "AND interviewer_id=:interviewerId "
			+ ") "
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") "
			+ ") as t", nativeQuery = true)
	Map<String, Long> getStateCountSumByInterviewer(@Param("campaignIds") List<String> campaignId,
			@Param("interviewerId") String interviewerId, @Param("ouIds") List<String> ouIds, @Param("date") Long date);

	@Query(value = "SELECT "
			+ "SUM(CASE WHEN type='NVM' THEN 1 ELSE 0 END) AS nvmCount, "
			+ "SUM(CASE WHEN type='NNS' THEN 1 ELSE 0 END) AS nnsCount, "
			+ "SUM(CASE WHEN type='ANV' THEN 1 ELSE 0 END) AS anvCount, "
			+ "SUM(CASE WHEN type='VIN' THEN 1 ELSE 0 END) AS vinCount, "
			+ "SUM(CASE WHEN type='VIC' THEN 1 ELSE 0 END) AS vicCount, "
			+ "SUM(CASE WHEN type='PRC' THEN 1 ELSE 0 END) AS prcCount, "
			+ "SUM(CASE WHEN type='AOC' THEN 1 ELSE 0 END) AS aocCount, "
			+ "SUM(CASE WHEN type='APS' THEN 1 ELSE 0 END) AS apsCount, "
			+ "SUM(CASE WHEN type='INS' THEN 1 ELSE 0 END) AS insCount, "
			+ "SUM(CASE WHEN type='WFT' THEN 1 ELSE 0 END) AS wftCount, "
			+ "SUM(CASE WHEN type='WFS' THEN 1 ELSE 0 END) AS wfsCount, "
			+ "SUM(CASE WHEN type='TBR' THEN 1 ELSE 0 END) AS tbrCount, "
			+ "SUM(CASE WHEN type='FIN' THEN 1 ELSE 0 END) AS finCount, "
			+ "SUM(CASE WHEN type='CLO' THEN 1 ELSE 0 END) AS cloCount, "
			+ "SUM(CASE WHEN type='NVA' THEN 1 ELSE 0 END) AS nvaCount, "
			+ "COUNT(1) AS total "
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM state WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN ("
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id IN (:ouIds) "
			+ "AND interviewer_id IS NULL "
			+ ") "
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") "
			+ ") as t", nativeQuery = true)
	Map<String, Long> getStateCountNotAttributed(@Param("campaignId") String campaignId,
			@Param("ouIds") List<String> ouIds,
			@Param("date") Long date);


	@Query(value = "SELECT "
			+ "SUM(CASE WHEN type='NVM' THEN 1 ELSE 0 END) AS nvmCount, "
			+ "SUM(CASE WHEN type='NNS' THEN 1 ELSE 0 END) AS nnsCount, "
			+ "SUM(CASE WHEN type='ANV' THEN 1 ELSE 0 END) AS anvCount, "
			+ "SUM(CASE WHEN type='VIN' THEN 1 ELSE 0 END) AS vinCount, "
			+ "SUM(CASE WHEN type='VIC' THEN 1 ELSE 0 END) AS vicCount, "
			+ "SUM(CASE WHEN type='PRC' THEN 1 ELSE 0 END) AS prcCount, "
			+ "SUM(CASE WHEN type='AOC' THEN 1 ELSE 0 END) AS aocCount, "
			+ "SUM(CASE WHEN type='APS' THEN 1 ELSE 0 END) AS apsCount, "
			+ "SUM(CASE WHEN type='INS' THEN 1 ELSE 0 END) AS insCount, "
			+ "SUM(CASE WHEN type='WFT' THEN 1 ELSE 0 END) AS wftCount, "
			+ "SUM(CASE WHEN type='WFS' THEN 1 ELSE 0 END) AS wfsCount, "
			+ "SUM(CASE WHEN type='TBR' THEN 1 ELSE 0 END) AS tbrCount, "
			+ "SUM(CASE WHEN type='FIN' THEN 1 ELSE 0 END) AS finCount, "
			+ "SUM(CASE WHEN type='CLO' THEN 1 ELSE 0 END) AS cloCount, "
			+ "SUM(CASE WHEN type='NVA' THEN 1 ELSE 0 END) AS nvaCount, "
			+ "COUNT(1) AS total "
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM state WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN ("
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id=?1 "
			+ ") "
			+ "AND (date<=?2 OR ?2<0) GROUP BY survey_unit_id"
			+ ") "
			+ ") as t", nativeQuery = true)
	Map<String, Long> getStateCountByCampaignId(String campaignId, Long date);

	@Query(value = "SELECT "
			+ "COUNT(1) AS total "
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date "
			+ "FROM state "
			+ "WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN ("
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id IN (:ouIds) "
			+ "AND interviewer_id=:interviewerId "
			+ ") "
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") "
			+ ") as t", nativeQuery = true)
	Long getTotalStateCount(@Param("campaignId") String campaignId,
			@Param("interviewerId") String interviewerId, @Param("ouIds") List<String> ouIds,
			@Param("date") Long date);


	@Query(value = "SELECT "
			+ "su.campaign_id AS entityId, "
			+ "SUM(CASE WHEN s.type='NVM' THEN 1 ELSE 0 END) AS nvmCount, "
			+ "SUM(CASE WHEN s.type='NNS' THEN 1 ELSE 0 END) AS nnsCount, "
			+ "SUM(CASE WHEN s.type='ANV' THEN 1 ELSE 0 END) AS anvCount, "
			+ "SUM(CASE WHEN s.type='VIN' THEN 1 ELSE 0 END) AS vinCount, "
			+ "SUM(CASE WHEN s.type='VIC' THEN 1 ELSE 0 END) AS vicCount, "
			+ "SUM(CASE WHEN s.type='PRC' THEN 1 ELSE 0 END) AS prcCount, "
			+ "SUM(CASE WHEN s.type='AOC' THEN 1 ELSE 0 END) AS aocCount, "
			+ "SUM(CASE WHEN s.type='APS' THEN 1 ELSE 0 END) AS apsCount, "
			+ "SUM(CASE WHEN s.type='INS' THEN 1 ELSE 0 END) AS insCount, "
			+ "SUM(CASE WHEN s.type='WFT' THEN 1 ELSE 0 END) AS wftCount, "
			+ "SUM(CASE WHEN s.type='WFS' THEN 1 ELSE 0 END) AS wfsCount, "
			+ "SUM(CASE WHEN s.type='TBR' THEN 1 ELSE 0 END) AS tbrCount, "
			+ "SUM(CASE WHEN s.type='FIN' THEN 1 ELSE 0 END) AS finCount, "
			+ "SUM(CASE WHEN s.type='CLO' THEN 1 ELSE 0 END) AS cloCount, "
			+ "SUM(CASE WHEN s.type='NVA' THEN 1 ELSE 0 END) AS nvaCount, "
			+ "COUNT(1) AS total "
			+ "FROM survey_unit su "
			+ "JOIN (SELECT survey_unit_id, MAX(date) as max_date FROM state GROUP BY survey_unit_id) last_state "
			+ "ON su.id = last_state.survey_unit_id "
			+ "JOIN state s ON s.survey_unit_id = last_state.survey_unit_id AND s.date = last_state.max_date "
			+ "WHERE su.campaign_id IN (:campaignIds) "
			+ "AND su.organization_unit_id IN (:ouIds) "
			+ "AND (s.date<=:date OR :date<0) "
			+ "GROUP BY su.campaign_id",
			nativeQuery = true)
	List<StateCount> findGroupedByCampaign(@Param("campaignIds") List<String> campaignIds,
										   @Param("ouIds") List<String> ouIds, @Param("date") Long date);


	@Query(value = """
        SELECT
            su.organization_unit_id AS entityId,
            SUM(CASE WHEN s.type='NVM' THEN 1 ELSE 0 END) AS nvmCount,
            SUM(CASE WHEN s.type='NNS' THEN 1 ELSE 0 END) AS nnsCount,
            SUM(CASE WHEN s.type='ANV' THEN 1 ELSE 0 END) AS anvCount,
            SUM(CASE WHEN s.type='VIN' THEN 1 ELSE 0 END) AS vinCount,
            SUM(CASE WHEN s.type='VIC' THEN 1 ELSE 0 END) AS vicCount,
            SUM(CASE WHEN s.type='PRC' THEN 1 ELSE 0 END) AS prcCount,
            SUM(CASE WHEN s.type='AOC' THEN 1 ELSE 0 END) AS aocCount,
            SUM(CASE WHEN s.type='APS' THEN 1 ELSE 0 END) AS apsCount,
            SUM(CASE WHEN s.type='INS' THEN 1 ELSE 0 END) AS insCount,
            SUM(CASE WHEN s.type='WFT' THEN 1 ELSE 0 END) AS wftCount,
            SUM(CASE WHEN s.type='WFS' THEN 1 ELSE 0 END) AS wfsCount,
            SUM(CASE WHEN s.type='TBR' THEN 1 ELSE 0 END) AS tbrCount,
            SUM(CASE WHEN s.type='FIN' THEN 1 ELSE 0 END) AS finCount,
            SUM(CASE WHEN s.type='CLO' THEN 1 ELSE 0 END) AS cloCount,
            SUM(CASE WHEN s.type='NVA' THEN 1 ELSE 0 END) AS nvaCount,
            COUNT(1) AS total
        FROM survey_unit su
        JOIN (
            SELECT survey_unit_id, MAX(date) AS max_date
            FROM state
            GROUP BY survey_unit_id
        ) last_state
            ON su.id = last_state.survey_unit_id
        JOIN state s
            ON s.survey_unit_id = last_state.survey_unit_id
           AND s.date = last_state.max_date
        WHERE su.campaign_id = :campaignId
          AND su.organization_unit_id IN (:ouIds)
          AND (s.date <= :dateToUse OR :dateToUse < 0)
        GROUP BY su.organization_unit_id
        """, nativeQuery = true)
	List<StateCount> findGroupedByOu(@Param("campaignId") String campaignId,
													   @Param("ouIds") List<String> ouIds,
													   @Param("dateToUse") Long dateToUse);


}
