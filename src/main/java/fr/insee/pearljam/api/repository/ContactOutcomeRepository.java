package fr.insee.pearljam.api.repository;

import fr.insee.pearljam.infrastructure.surveyunit.entity.ContactOutcomeDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
* ContactOutcomeRepository is the repository using to access to ContactOutcome table in DB
* 
* @author scorcaud
* 
*/
public interface ContactOutcomeRepository extends JpaRepository<ContactOutcomeDB, Long> {

	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='INA' THEN 1 ELSE 0 END) AS inaCount, "
			+ "SUM(CASE WHEN type='REF' THEN 1 ELSE 0 END) AS refCount, "
			+ "SUM(CASE WHEN type='IMP' THEN 1 ELSE 0 END) AS impCount, "
			+ "SUM(CASE WHEN type='UCD' THEN 1 ELSE 0 END) AS ucdCount, "
			+ "SUM(CASE WHEN type='UTR' THEN 1 ELSE 0 END) AS utrCount, "
			+ "SUM(CASE WHEN type='ALA' THEN 1 ELSE 0 END) AS alaCount, "
			+ "SUM(CASE WHEN type='NUH' THEN 1 ELSE 0 END) AS nuhCount, "
			+ "SUM(CASE WHEN type='DUK' THEN 1 ELSE 0 END) AS dukCount, "
			+ "SUM(CASE WHEN type='DUU' THEN 1 ELSE 0 END) AS duuCount, "
			+ "SUM(CASE WHEN type='NOA' THEN 1 ELSE 0 END) AS noaCount, "
			+ "COUNT(1) AS total "
		+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM contact_outcome WHERE (survey_unit_id, date) IN ("
				+ "SELECT survey_unit_id, MAX(date) FROM contact_outcome WHERE survey_unit_id IN (" 
					+ "SELECT id FROM survey_unit su "
					+ "WHERE campaign_id=:campaignId " 
					+ "AND organization_unit_id IN (:ouIds) "
					// Last state is TBR or FIN or CLO
					+ "AND EXISTS ("
						+ "SELECT 1 FROM state "
						+ "WHERE survey_unit_id = su.id "
						+ "AND date=("
							+ "SELECT MAX(date) FROM state "
							+ "WHERE survey_unit_id = su.id"
						+ ") "
						+ "AND type IN ('TBR', 'FIN', 'CLO')"
					+ ") "
					+ "AND interviewer_id=:interviewerId "
				+ ") " 
				+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") "
		+ ") as t", nativeQuery = true)
	Map<String, Long> findContactOutcomeTypeByInterviewerAndCampaign(@Param("campaignId") String campaignId, 
			@Param("interviewerId") String interviewerId, @Param("ouIds") List<String> ouIds, 
			@Param("date") Long date);
	
	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='INA' THEN 1 ELSE 0 END) AS inaCount, "
			+ "SUM(CASE WHEN type='REF' THEN 1 ELSE 0 END) AS refCount, "
			+ "SUM(CASE WHEN type='IMP' THEN 1 ELSE 0 END) AS impCount, "
			+ "SUM(CASE WHEN type='UCD' THEN 1 ELSE 0 END) AS ucdCount, "
			+ "SUM(CASE WHEN type='UTR' THEN 1 ELSE 0 END) AS utrCount, "
			+ "SUM(CASE WHEN type='ALA' THEN 1 ELSE 0 END) AS alaCount, "
			+ "SUM(CASE WHEN type='NUH' THEN 1 ELSE 0 END) AS nuhCount, "
			+ "SUM(CASE WHEN type='DUK' THEN 1 ELSE 0 END) AS dukCount, "
			+ "SUM(CASE WHEN type='DUU' THEN 1 ELSE 0 END) AS duuCount, "
			+ "SUM(CASE WHEN type='NOA' THEN 1 ELSE 0 END) AS noaCount, "
			+ "COUNT(1) AS total "
		+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM contact_outcome WHERE (survey_unit_id, date) IN ("
				+ "SELECT survey_unit_id, MAX(date) FROM contact_outcome WHERE survey_unit_id IN (" 
					+ "SELECT id FROM survey_unit su "
					+ "WHERE campaign_id=:campaignId " 
					+ "AND organization_unit_id IN (:ouIds) "
					// Last state is TBR or FIN or CLO
					+ "AND EXISTS ("
						+ "SELECT 1 FROM state "
						+ "WHERE survey_unit_id = su.id "
						+ "AND date=("
							+ "SELECT MAX(date) FROM state "
							+ "WHERE survey_unit_id = su.id"
						+ ") "
						+ "AND type IN ('TBR', 'FIN', 'CLO')"
					+ ") "
					+ "AND interviewer_id IS NULL "
				+ ") " 
				+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id"
			+ ") "
		+ ") as t", nativeQuery = true)
	Map<String, Long> findContactOutcomeTypeNotAttributed(@Param("campaignId") String campaignId, 
			@Param("ouIds") List<String> ouIds, 
			@Param("date") Long date);
	
	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='INA' THEN 1 ELSE 0 END) AS inaCount, "
			+ "SUM(CASE WHEN type='REF' THEN 1 ELSE 0 END) AS refCount, "
			+ "SUM(CASE WHEN type='IMP' THEN 1 ELSE 0 END) AS impCount, "
			+ "SUM(CASE WHEN type='UCD' THEN 1 ELSE 0 END) AS ucdCount, "
			+ "SUM(CASE WHEN type='UTR' THEN 1 ELSE 0 END) AS utrCount, "
			+ "SUM(CASE WHEN type='ALA' THEN 1 ELSE 0 END) AS alaCount, "
			+ "SUM(CASE WHEN type='NUH' THEN 1 ELSE 0 END) AS nuhCount, "
			+ "SUM(CASE WHEN type='DUK' THEN 1 ELSE 0 END) AS dukCount, "
			+ "SUM(CASE WHEN type='DUU' THEN 1 ELSE 0 END) AS duuCount, "
			+ "SUM(CASE WHEN type='NOA' THEN 1 ELSE 0 END) AS noaCount, "
			+ "COUNT(1) AS total "
		+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM contact_outcome WHERE (survey_unit_id, date) IN ("
				+ "SELECT survey_unit_id, MAX(date) FROM contact_outcome WHERE survey_unit_id IN (" 
					+ "SELECT id FROM survey_unit su "
					+ "WHERE campaign_id=?1 "
					// Last state is TBR or FIN or CLO
					+ "AND EXISTS ("
						+ "SELECT 1 FROM state "
						+ "WHERE survey_unit_id = su.id "
						+ "AND date=("
							+ "SELECT MAX(date) FROM state "
							+ "WHERE survey_unit_id = su.id"
						+ ") "
						+ "AND type IN ('TBR', 'FIN', 'CLO')"
					+ ") "
				+ ") " 
				+ "AND (date<=?2 OR ?2<0) GROUP BY survey_unit_id"
			+ ") "
		+ ") as t", nativeQuery = true)
	Map<String,Long> getContactOutcomeTypeCountByCampaignId(String campaignId, Long date);

	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='INA' THEN 1 ELSE 0 END) AS inaCount, "
			+ "SUM(CASE WHEN type='REF' THEN 1 ELSE 0 END) AS refCount, "
			+ "SUM(CASE WHEN type='IMP' THEN 1 ELSE 0 END) AS impCount, "
			+ "SUM(CASE WHEN type='UCD' THEN 1 ELSE 0 END) AS ucdCount, "
			+ "SUM(CASE WHEN type='UTR' THEN 1 ELSE 0 END) AS utrCount, "
			+ "SUM(CASE WHEN type='ALA' THEN 1 ELSE 0 END) AS alaCount, "
			+ "SUM(CASE WHEN type='NUH' THEN 1 ELSE 0 END) AS nuhCount, "
			+ "SUM(CASE WHEN type='DUK' THEN 1 ELSE 0 END) AS dukCount, "
			+ "SUM(CASE WHEN type='DUU' THEN 1 ELSE 0 END) AS duuCount, "
			+ "SUM(CASE WHEN type='NOA' THEN 1 ELSE 0 END) AS noaCount, "
			+ "COUNT(1) AS total "
		+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM contact_outcome WHERE (survey_unit_id, date) IN ("
				+ "SELECT survey_unit_id, MAX(date) FROM contact_outcome WHERE survey_unit_id IN (" 
					+ "SELECT id FROM survey_unit su "
					+ "WHERE campaign_id=?1 "
					// Last state is TBR or FIN or CLO
					+ "AND EXISTS ("
						+ "SELECT 1 FROM state "
						+ "WHERE survey_unit_id = su.id "
						+ "AND date=("
							+ "SELECT MAX(date) FROM state "
							+ "WHERE survey_unit_id = su.id"
						+ ") "
						+ "AND type IN ('TBR', 'FIN', 'CLO')"
					+ ") "
					+ "AND organization_unit_id=?2 "
				+ ") "
				+ "AND (date<=?3 OR ?3<0) GROUP BY survey_unit_id"
			+ ") " 
		+ ") as t", nativeQuery = true)
	Map<String,Long> getContactOutcomeTypeCountByCampaignAndOU(String campaignId, String organizationalUnitId, Long date);
}
