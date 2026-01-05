package fr.insee.pearljam.api.repository;

import fr.insee.pearljam.api.domain.ClosingCause;
import fr.insee.pearljam.domain.count.model.ClosingCauseCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ClosingCauseRepository extends JpaRepository<ClosingCause, Long> {
	
	List<ClosingCause> findBySurveyUnitId(String surveyUnitId);

	void deleteBySurveyUnitId(String surveyUnitId);
	
	@Query(value = "SELECT " 
				+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
				+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
				+ "SUM(CASE WHEN type='NPX' THEN 1 ELSE 0 END) AS npxCount, "
				+ "SUM(CASE WHEN type='ROW' THEN 1 ELSE 0 END) AS rowCount "
			+ "FROM ( "
				+ "SELECT DISTINCT(survey_unit_id), type, date "
				+ "FROM closing_cause "
				+ "WHERE (survey_unit_id, date) IN ("
					+ "SELECT survey_unit_id, MAX(date) FROM closing_cause WHERE survey_unit_id IN (" 
						+ "SELECT id FROM survey_unit su "
						+ "WHERE campaign_id=:campaignId " 
						+ "AND organization_unit_id IN (:ouIds) "
						// SU state needs to be CLO
      					+ "AND 'CLO' IN ( "
      						+ "SELECT type FROM state WHERE (survey_unit_id, date) IN ( "
      							+ "SELECT survey_unit_id, MAX(date) "
      							+ "FROM state "
      							+ "WHERE survey_unit_id = su.id GROUP BY survey_unit_id)"
						+ ") "
						+ "AND interviewer_id=:interviewerId "
					+ ") " 
					+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) "
			+ ") as t", nativeQuery = true)
	Map<String,Long> getStateClosedByClosingCauseCount(@Param("campaignId") String campaignId, 
			@Param("interviewerId") String interviewerId, @Param("ouIds") List<String> ouIds, 
			@Param("date") Long date);
	
	@Query(value = "SELECT "
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
			+ "SUM(CASE WHEN type='NPX' THEN 1 ELSE 0 END) AS npxCount, "
			+ "SUM(CASE WHEN type='ROW' THEN 1 ELSE 0 END) AS rowCount "
		+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM closing_cause WHERE (survey_unit_id, date) IN ("
				+ "SELECT survey_unit_id, MAX(date) FROM closing_cause WHERE survey_unit_id IN (" 
					+ "SELECT id FROM survey_unit su "
					+ "WHERE campaign_id=:campaignId " 
					+ "AND organization_unit_id IN (:ouIds) "
					// SU state needs to be CLO
					+ "AND 'CLO' IN ( "
						+ "SELECT type FROM state WHERE (survey_unit_id, date) IN ( "
							+ "SELECT survey_unit_id, MAX(date) "
							+ "FROM state where survey_unit_id = su.id GROUP BY survey_unit_id"
						+ ")"
					+ ") "
					+ "AND interviewer_id IS NULL "
				+ ") " 
				+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") "
		+ ") as t", nativeQuery = true)
	Map<String,Long> getClosingCauseCountNotAttributed(@Param("campaignId") String campaignId, 
			@Param("ouIds") List<String> ouIds, 
			@Param("date") Long date);
	
	@Query(value = "SELECT "
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
			+ "SUM(CASE WHEN type='NPX' THEN 1 ELSE 0 END) AS npxCount, "
			+ "SUM(CASE WHEN type='ROW' THEN 1 ELSE 0 END) AS rowCount " 
		+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM closing_cause WHERE (survey_unit_id, date) IN ("
				+ "SELECT survey_unit_id, MAX(date) FROM closing_cause WHERE survey_unit_id IN (" 
					+ "SELECT id FROM survey_unit su "
					+ "WHERE campaign_id IN (:campaignIds) " 
					+ "AND organization_unit_id IN (:ouIds) "
					// SU state needs to be CLO
					+ "AND 'CLO' IN ( "
						+ "SELECT type FROM state WHERE (survey_unit_id, date) IN ( "
							+ "SELECT survey_unit_id, MAX(date) "
							+ "FROM state where survey_unit_id = su.id GROUP BY survey_unit_id"
						+ ")"
					+ ") "
					+ "AND interviewer_id=:interviewerId "
				+ ") "
				+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") " 
		+ ") as t", nativeQuery = true)
	Map<String,Long> getClosingCauseCountSumByInterviewer(@Param("campaignIds") List<String> campaignId,
			@Param("interviewerId") String interviewerId, @Param("ouIds") List<String> ouIds, @Param("date") Long date);
	
	@Query(value = "SELECT "
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
			+ "SUM(CASE WHEN type='NPX' THEN 1 ELSE 0 END) AS npxCount, "
			+ "SUM(CASE WHEN type='ROW' THEN 1 ELSE 0 END) AS rowCount " 
		+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM closing_cause WHERE (survey_unit_id, date) IN ("
				+ "SELECT survey_unit_id, MAX(date) FROM closing_cause WHERE survey_unit_id IN (" 
					+ "SELECT id FROM survey_unit su "
					+ "WHERE campaign_id=?1 "
					// SU state needs to be CLO
					+ "AND 'CLO' IN ( "
						+ "SELECT type FROM state WHERE (survey_unit_id, date) IN ( "
							+ "SELECT survey_unit_id, MAX(date) "
							+ "FROM state where survey_unit_id = su.id GROUP BY survey_unit_id"
						+ ")"
					+ ") "
					+ "AND organization_unit_id =?2"
				+ ") "
				+ "AND (date<=?3 OR ?3<0) GROUP BY survey_unit_id"
			+ ") " 
		+ ") as t", nativeQuery = true)
	Map<String,Long> getClosingCauseCountByCampaignAndOU(String campaignId, String organizationalUnitId, Long date);

	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
			+ "SUM(CASE WHEN type='NPX' THEN 1 ELSE 0 END) AS npxCount, "
			+ "SUM(CASE WHEN type='ROW' THEN 1 ELSE 0 END) AS rowCount " 
		+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM closing_cause WHERE (survey_unit_id, date) IN ("
				+ "SELECT survey_unit_id, MAX(date) FROM closing_cause WHERE survey_unit_id IN (" 
					+ "SELECT id FROM survey_unit su "
					+ "WHERE campaign_id=?1 "
					// SU state needs to be CLO
					+ "AND 'CLO' IN ( "
						+ "SELECT type FROM state WHERE (survey_unit_id, date) IN ( "
							+ "SELECT survey_unit_id, MAX(date) "
							+ "FROM state where survey_unit_id = su.id GROUP BY survey_unit_id"
						+ ")"
					+ ") "
				+ ") " 
				+ "AND (date<=?2 OR ?2<0) GROUP BY survey_unit_id"
			+ ") "
		+ ") as t", nativeQuery = true)
	Map<String, Long> getClosingCauseCountByCampaignId(String campaignId, Long date);

	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
			+ "SUM(CASE WHEN type='NPX' THEN 1 ELSE 0 END) AS npxCount, "
			+ "SUM(CASE WHEN type='ROW' THEN 1 ELSE 0 END) AS rowCount "
		+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date "
			+ "FROM closing_cause "
			+ "WHERE (survey_unit_id, date) IN ("
				+ "SELECT survey_unit_id, MAX(date) FROM closing_cause WHERE survey_unit_id IN (" 
					+ "SELECT id FROM survey_unit su "
					+ "WHERE campaign_id=:campaignId " 
					+ "AND organization_unit_id IN (:ouIds) "
					+ "AND interviewer_id=:interviewerId "
				+ ") " 
				+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) "
		+ ") as t", nativeQuery = true)
	Map<String, Long> getClosingCauseCount(@Param("campaignId") String campaignId, 
			@Param("interviewerId") String interviewerId, @Param("ouIds") List<String> ouIds, 
			@Param("date") Long date);

	@Query(value = """
			SELECT
			su.campaign_id as campaignId,
			SUM(CASE WHEN cc.type='NPA' THEN 1 ELSE 0 END) AS npaCount,
			SUM(CASE WHEN cc.type='NPI' THEN 1 ELSE 0 END) AS npiCount,
			SUM(CASE WHEN cc.type='NPX' THEN 1 ELSE 0 END) AS npxCount,
			SUM(CASE WHEN cc.type='ROW' THEN 1 ELSE 0 END) AS rowCount
			FROM survey_unit su
			JOIN (SELECT survey_unit_id, MAX(date) as max_date FROM closing_cause GROUP BY survey_unit_id) last_closing
			ON su.id = last_closing.survey_unit_id
			JOIN closing_cause cc
			ON cc.survey_unit_id = last_closing.survey_unit_id AND cc.date = last_closing.max_date
			WHERE su.campaign_id IN (:campaignIds)
			AND su.organization_unit_id IN (:ouIds)
			AND (cc.date<=:date OR :date<0)
			AND 'CLO' IN (
			SELECT s.type FROM state s WHERE s.survey_unit_id = su.id
			AND s.date = (SELECT MAX(date) FROM state WHERE survey_unit_id = su.id)
			)
			GROUP BY su.campaign_id
			""",
			nativeQuery = true)
	List<ClosingCauseCount> getStateClosedByClosingCauseCountByCampaigns(
			@Param("campaignIds") List<String> campaignIds,
			@Param("ouIds") List<String> ouIds,
			@Param("date") Long date);
}
