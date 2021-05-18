package fr.insee.pearljam.api.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.insee.pearljam.api.domain.ClosingCause;

public interface ClosingCauseRepository extends JpaRepository<ClosingCause, Long> {
	
	List<ClosingCause> findBySurveyUnitId(String surveyUnitId);

	void deleteBySurveyUnitId(String surveyUnitId);
	
	@Query(value = "SELECT " 
				+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
				+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
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
						// SU needs to be visible at the given date for the OU its in
						+ "AND (:date<0 OR EXISTS ("
							+ "SELECT 1 FROM visibility "
							+ "WHERE campaign_id=:campaignId "
							+ "AND organization_unit_id = su.organization_unit_id "
							+ "AND management_start_date<=:date " 
							+ "AND collection_start_date<=:date " 
							+ "AND collection_end_date>:date )"
						+ ") "
						+ "AND interviewer_id=:interviewerId "
					+ ") " 
					+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) "
			+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getClosingCauseCount(@Param("campaignId") String campaignId, 
			@Param("interviewerId") String interviewerId, @Param("ouIds") List<String> ouIds, 
			@Param("date") Long date);
	
	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
			+ "SUM(CASE WHEN type='ROW' THEN 1 ELSE 0 END) AS rowCount " 
		+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM closing_cause WHERE (survey_unit_id, date) IN ("
				+ "SELECT survey_unit_id, MAX(date) FROM closing_cause WHERE survey_unit_id IN (" 
					+ "SELECT id FROM survey_unit su "
					+ "WHERE campaign_id=:campaignId " 
					// SU state needs to be CLO
					+ "AND 'CLO' IN ( "
						+ "SELECT type FROM state WHERE (survey_unit_id, date) IN ( "
							+ "SELECT survey_unit_id, MAX(date) "
							+ "FROM state where survey_unit_id = su.id GROUP BY survey_unit_id"
						+ ")"
					+ ") "
					// SU needs to be visible at the given date for the OU its in
					+ "AND (:date<0 OR EXISTS ("
						+ "SELECT 1 FROM visibility "
						+ "WHERE campaign_id=:campaignId "
						+ "AND organization_unit_id = su.organization_unit_id "
						+ "AND management_start_date<=:date " 
						+ "AND collection_start_date<=:date " 
						+ "AND collection_end_date>:date )"
					+ ") "
					+ "AND organization_unit_id IN (:ouIds)"
				+ ") " 
				+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") " 
		+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getClosingCauseCountSumByCampaign(@Param("campaignId") String campaignId,
			@Param("ouIds") List<String> ouIds, @Param("date") Long date);

	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
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
					// SU needs to be visible at the given date for the OU its in
					+ "AND (:date<0 OR EXISTS ("
						+ "SELECT 1 FROM visibility "
						+ "WHERE campaign_id=:campaignId "
						+ "AND organization_unit_id = su.organization_unit_id "
						+ "AND management_start_date<=:date " 
						+ "AND collection_start_date<=:date " 
						+ "AND collection_end_date>:date )"
					+ ") "
					+ "AND interviewer_id IS NULL "
				+ ") " 
				+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") "
		+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getClosingCauseCountNotAttributed(@Param("campaignId") String campaignId, 
			@Param("ouIds") List<String> ouIds, 
			@Param("date") Long date);
	
	@Query(value = "SELECT "
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
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
					// SU needs to be visible at the given date for the OU its in
					+ "AND (:date<0 OR EXISTS ("
						+ "SELECT 1 FROM visibility "
						+ "WHERE campaign_id=:campaignId "
						+ "AND organization_unit_id = su.organization_unit_id "
						+ "AND management_start_date<=:date " 
						+ "AND collection_start_date<=:date " 
						+ "AND collection_end_date>:date)"
					+ ") "
					+ "AND interviewer_id=:interviewerId "
				+ ") "
				+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") " 
		+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getClosingCauseCountSumByInterviewer(@Param("campaignIds") List<String> campaignId,
			@Param("interviewerId") String interviewerId, @Param("ouIds") List<String> ouIds, @Param("date") Long date);
	
	@Query(value = "SELECT "
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
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
					// SU needs to be visible at the given date for the OU its in
					+ "AND (:date<0 OR EXISTS ("
						+ "SELECT 1 FROM visibility "
						+ "WHERE campaign_id=:campaignId "
						+ "AND organization_unit_id = su.organization_unit_id "
						+ "AND management_start_date<=:date " 
						+ "AND collection_start_date<=:date " 
						+ "AND collection_end_date>:date )"
					+ ") "
					+ "AND interviewer_id IS NULL "
				+ ") "
				+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") " 
		+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getClosingCauseCountNotAttributed(@Param("campaignIds") List<String> campaignId,
			@Param("ouIds") List<String> ouIds, @Param("date") Long date);
	
	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
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
					// SU needs to be visible at the given date for the OU its in
					+ "AND (?3<0 OR EXISTS ("
						+ "SELECT 1 FROM visibility "
						+ "WHERE campaign_id=?1 "
						+ "AND organization_unit_id = su.organization_unit_id "
						+ "AND management_start_date<=?3 " 
						+ "AND collection_start_date<=?3 " 
						+ "AND collection_end_date>?3 )"
					+ ") "
					+ "AND organization_unit_id =?2"
				+ ") "
				+ "AND (date<=?3 OR ?3<0) GROUP BY survey_unit_id"
			+ ") " 
		+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getClosingCauseCountByCampaignAndOU(String campaignId, String organizationalUnitId, Long date);

	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
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
					// SU needs to be visible at the given date for the OU its in
					+ "AND (?2<0 OR EXISTS ("
						+ "SELECT 1 FROM visibility "
						+ "WHERE campaign_id=?1 "
						+ "AND organization_unit_id = su.organization_unit_id "
						+ "AND management_start_date<=?2 " 
						+ "AND collection_start_date<=?2 " 
						+ "AND collection_end_date>?2 )"
					+ ") "
				+ ") " 
				+ "AND (date<=?2 OR ?2<0) GROUP BY survey_unit_id"
			+ ") "
		+ ") as t", nativeQuery = true)
	Map<String, BigInteger> getClosingCauseCountByCampaignId(String campaignId, Long date);
}
