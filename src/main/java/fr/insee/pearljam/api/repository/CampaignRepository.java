package fr.insee.pearljam.api.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.dto.message.VerifyNameResponseDto;

/**
 * CampaignRepository is the repository using to access to Campaign table in DB
 * 
 * @author scorcaud
 * 
 */
public interface CampaignRepository extends JpaRepository<Campaign, String> {

	Optional<Campaign> findByIdIgnoreCase(String id);

	@Query(value = "SELECT c.id FROM Campaign c")
	List<String> findAllIds();

	@Query(value = "SELECT DISTINCT(campaign_id) FROM visibility WHERE " 
			+ "organization_unit_id IN (:OuIds) "
			+ "AND management_start_date <= :date " 
			+ "AND collection_start_date <= :date " 
			+ "AND collection_end_date > :date  ", nativeQuery = true)
	List<String> findAllIdsVisible(@Param("OuIds") List<String> ouIds, @Param("date") Long date);

	@Query(value = "SELECT camp.id " 
			+ "FROM campaign camp " 
			+ "INNER JOIN visibility vi ON vi.campaign_id = camp.id "
			+ "INNER JOIN organization_unit ou ON ou.id = vi.organization_unit_id "
			+ "WHERE ou.id ILIKE ?1", nativeQuery = true)
	List<String> findIdsByOuId(String ouId);

	@Query(value = "SELECT new fr.insee.pearljam.api.dto.campaign.CampaignDto(camp.id, camp.label) " 
			+ "FROM Campaign camp " 
			+ "WHERE camp.id=?1")
	CampaignDto findDtoById(String id);

	@Query(value = "SELECT 1 " 
			+ "FROM preference pref " 
			+ "WHERE pref.id_user ILIKE ?1 "
			+ "AND pref.id_campaign = ?2", nativeQuery = true)
	List<Integer> checkCampaignPreferences(String userId, String campaignId);

	@Query("SELECT "
			+ "new fr.insee.pearljam.api.dto.interviewer.InterviewerDto(interv.id, interv.firstName, interv.lastName, COUNT(su.interviewer)) "
			+ "FROM SurveyUnit su " 
			+ "INNER JOIN Interviewer interv ON su.interviewer.id = interv.id "
			+ "WHERE su.campaign.id=?1 " 
			+ "AND (su.organizationUnit.id=?2 OR ?2='GUEST') " 
			+ "GROUP BY interv.id")
	List<InterviewerDto> findInterviewersDtoByCampaignIdAndOrganisationUnitId(String id, String organizationUnitId);

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
			+ "SUM(CASE WHEN type='QNA' THEN 1 ELSE 0 END) AS qnaCount, "
			+ "SUM(CASE WHEN type='QNA' AND EXISTS (SELECT 1 FROM state s where s.type ='FIN' AND s.survey_unit_id = t.survey_unit_id) THEN 1 ELSE 0 END) AS qnaFinCount, " 
			+ "SUM(CASE WHEN type='NVA' THEN 1 ELSE 0 END) AS nvaCount, " 
			+ "COUNT(1) AS total " 
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM state WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN (" 
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id=:campaignId " 
			+ "AND organization_unit_id IN (:ouIds) "
			// SU needs to be visible at the given date for the OU its in
			+ "AND (:date<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=:date " 
			+ "AND collection_start_date<=:date " 
			+ "AND collection_end_date>:date )) "
			//
			+ "AND interviewer_id=:interviewerId ) " 
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) "
			
			+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getStateCount(@Param("campaignId") String campaignId, 
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
			+ "AND organization_unit_id IN (:ouIds) "
			// SU needs to be visible at the given date for the OU its in
			+ "AND (:date<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=:date " 
			+ "AND collection_start_date<=:date " 
			+ "AND collection_end_date>:date )) "
			//
			+ "AND interviewer_id=:interviewerId ) " 
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) "
			+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getClausingCauseCount(@Param("campaignId") String campaignId, 
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
			+ "SUM(CASE WHEN type='QNA' THEN 1 ELSE 0 END) AS qnaCount, "
			+ "SUM(CASE WHEN type='QNA' AND EXISTS (SELECT 1 FROM state s where s.type ='FIN' AND s.survey_unit_id = t.survey_unit_id) THEN 1 ELSE 0 END) AS qnaFinCount, " 
			+ "SUM(CASE WHEN type='NVA' THEN 1 ELSE 0 END) AS nvaCount, " 
			+ "COUNT(1) AS total " 
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM state WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN (" 
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id=:campaignId " 
			// SU needs to be visible at the given date for the OU its in
			+ "AND (:date<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=:date " 
			+ "AND collection_start_date<=:date " 
			+ "AND collection_end_date>:date )) "
			//
			+ "AND organization_unit_id IN (:ouIds)) " 
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) " + ") as t", nativeQuery = true)
	Map<String,BigInteger> getStateCountSumByCampaign(@Param("campaignId") String campaignId,
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
			// SU needs to be visible at the given date for the OU its in
			+ "AND (:date<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=:date " 
			+ "AND collection_start_date<=:date " 
			+ "AND collection_end_date>:date )) "
			//
			+ "AND organization_unit_id IN (:ouIds)) " 
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) " + ") as t", nativeQuery = true)
	Map<String,BigInteger> getClausingCauseCountSumByCampaign(@Param("campaignId") String campaignId,
			@Param("ouIds") List<String> ouIds, @Param("date") Long date);

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
			+ "SUM(CASE WHEN type='QNA' THEN 1 ELSE 0 END) AS qnaCount, "
			+ "SUM(CASE WHEN type='QNA' AND EXISTS (SELECT 1 FROM state s where s.type ='FIN' AND s.survey_unit_id = t.survey_unit_id) THEN 1 ELSE 0 END) AS qnaFinCount, " 
			+ "SUM(CASE WHEN type='NVA' THEN 1 ELSE 0 END) AS nvaCount, "
			+ "COUNT(1) AS total " 
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM state WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN (" 
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id IN (:campaignIds) "
			// SU needs to be visible at the given date for the OU its in
			+ "AND (:date<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=:date " 
			+ "AND collection_start_date<=:date " 
			+ "AND collection_end_date>:date )) "
			//
			+ "AND organization_unit_id IN (:ouIds) " 
			+ "AND interviewer_id=:interviewerId ) "
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) " 
			+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getStateCountSumByInterviewer(@Param("campaignIds") List<String> campaignId,
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
			+ "SUM(CASE WHEN type='QNA' THEN 1 ELSE 0 END) AS qnaCount, "
			+ "SUM(CASE WHEN type='QNA' AND EXISTS (SELECT 1 FROM state s where s.type ='FIN' AND s.survey_unit_id = t.survey_unit_id) THEN 1 ELSE 0 END) AS qnaFinCount, " 
			+ "SUM(CASE WHEN type='NVA' THEN 1 ELSE 0 END) AS nvaCount, " 
			+ "COUNT(1) AS total " 
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM state WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN (" 
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id=:campaignId " 
			+ "AND organization_unit_id IN (:ouIds) "
			// SU needs to be visible at the given date for the OU its in
			+ "AND (:date<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=:date " 
			+ "AND collection_start_date<=:date " 
			+ "AND collection_end_date>:date )) "
			//
			+ "AND interviewer_id IS NULL ) " 
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) "
			
			+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getStateCountNotAttributed(@Param("campaignId") String campaignId, 
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
			+ "WHERE campaign_id=:campaignId " 
			+ "AND organization_unit_id IN (:ouIds) "
			// SU needs to be visible at the given date for the OU its in
			+ "AND (:date<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=:date " 
			+ "AND collection_start_date<=:date " 
			+ "AND collection_end_date>:date )) "
			//
			+ "AND interviewer_id IS NULL ) " 
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) "
			+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getClausingCauseCountNotAttributed(@Param("campaignId") String campaignId, 
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
			// SU needs to be visible at the given date for the OU its in
			+ "AND (:date<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=:date " 
			+ "AND collection_start_date<=:date " 
			+ "AND collection_end_date>:date )) "
			//
			+ "AND interviewer_id=:interviewerId ) "
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) " 
			+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getClausingCauseCountSumByInterviewer(@Param("campaignIds") List<String> campaignId,
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
			// SU needs to be visible at the given date for the OU its in
			+ "AND (:date<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=:campaignId "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=:date " 
			+ "AND collection_start_date<=:date " 
			+ "AND collection_end_date>:date )) "
			//
			+ "AND interviewer_id IS NULL ) "
			+ "AND (date<=:date OR :date<0) GROUP BY survey_unit_id) " 
			+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getClausingCauseCountNotAttributed(@Param("campaignIds") List<String> campaignId,
			@Param("ouIds") List<String> ouIds, @Param("date") Long date);
	

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
			+ "SUM(CASE WHEN type='QNA' THEN 1 ELSE 0 END) AS qnaCount, "
			+ "SUM(CASE WHEN type='QNA' AND EXISTS (SELECT 1 FROM state s where s.type ='FIN' AND s.survey_unit_id = t.survey_unit_id) THEN 1 ELSE 0 END) AS qnaFinCount, " 
			+ "SUM(CASE WHEN type='NVA' THEN 1 ELSE 0 END) AS nvaCount, "
			+ "COUNT(1) AS total " 
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM state WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN (" 
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id=?1 "
			// SU needs to be visible at the given date for the OU its in
			+ "AND (?3<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=?1 "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=?3 " 
			+ "AND collection_start_date<=?3 " 
			+ "AND collection_end_date>?3 )) "
			//
			+ "AND organization_unit_id =?2) "
			+ "AND (date<=?3 OR ?3<0) GROUP BY survey_unit_id) " 
			+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getStateCountByCampaignAndOU(String campaignId, String organizationalUnitId, Long date);

	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
			+ "SUM(CASE WHEN type='ROW' THEN 1 ELSE 0 END) AS rowCount " 
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM closing_cause WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM closing_cause WHERE survey_unit_id IN (" 
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id=?1 "
			// SU needs to be visible at the given date for the OU its in
			+ "AND (?3<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=?1 "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=?3 " 
			+ "AND collection_start_date<=?3 " 
			+ "AND collection_end_date>?3 )) "
			//
			+ "AND organization_unit_id =?2) "
			+ "AND (date<=?3 OR ?3<0) GROUP BY survey_unit_id) " 
			+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getClausingCauseCountByCampaignAndOU(String campaignId, String organizationalUnitId, Long date);

	
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
			+ "SUM(CASE WHEN type='QNA' THEN 1 ELSE 0 END) AS qnaCount, "
			+ "SUM(CASE WHEN type='QNA' AND EXISTS (SELECT 1 FROM state s where s.type ='FIN' AND s.survey_unit_id = t.survey_unit_id) THEN 1 ELSE 0 END) AS qnaFinCount, " 
			+ "SUM(CASE WHEN type='NVA' THEN 1 ELSE 0 END) AS nvaCount, " 
			+ "COUNT(1) AS total " 
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM state WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM state WHERE survey_unit_id IN (" 
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id=?1 "
			// SU needs to be visible at the given date for the OU its in
			+ "AND (?2<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=?1 "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=?2 " 
			+ "AND collection_start_date<=?2 " 
			+ "AND collection_end_date>?2 )) "
			//
			+ ") " 
			+ "AND (date<=?2 OR ?2<0) GROUP BY survey_unit_id) "
			+ ") as t", nativeQuery = true)
	Map<String,BigInteger> getStateCountByCampaignId(String campaignId, Long date);
	
	@Query(value = "SELECT " 
			+ "SUM(CASE WHEN type='NPA' THEN 1 ELSE 0 END) AS npaCount, "
			+ "SUM(CASE WHEN type='NPI' THEN 1 ELSE 0 END) AS npiCount, "
			+ "SUM(CASE WHEN type='ROW' THEN 1 ELSE 0 END) AS rowCount " 
			+ "FROM ( "
			+ "SELECT DISTINCT(survey_unit_id), type, date FROM closing_cause WHERE (survey_unit_id, date) IN ("
			+ "SELECT survey_unit_id, MAX(date) FROM closing_cause WHERE survey_unit_id IN (" 
			+ "SELECT id FROM survey_unit su "
			+ "WHERE campaign_id=?1 "
			
			// SU needs to be visible at the given date for the OU its in
			+ "AND (?2<0 OR EXISTS (SELECT 1 FROM visibility "
			+ "WHERE campaign_id=?1 "
			+ "AND organization_unit_id = su.organization_unit_id "
			+ "AND management_start_date<=?2 " 
			+ "AND collection_start_date<=?2 " 
			+ "AND collection_end_date>?2 )) "
			+ ") " 
			//
			+ "AND (date<=?2 OR ?2<0) GROUP BY survey_unit_id) "
			+ ") as t", nativeQuery = true)
	Map<String, BigInteger> getClausingCauseCountByCampaignId(String campaignId, Long date);

	@Query(value = "SELECT v.organization_unit_id FROM visibility v WHERE v.campaign_id=?1", nativeQuery = true)
	List<String> findAllOrganistionUnitIdByCampaignId(String campaignId);

	@Query("SELECT new fr.insee.pearljam.api.dto.message.VerifyNameResponseDto(camp.id,  'campaign', camp.label) "
			+ "FROM Campaign camp " + "INNER JOIN Visibility vi ON vi.campaign.id = camp.id "
			+ "WHERE (vi.organizationUnit.id in (:ouIds) OR 'GUEST' in (:ouIds) "
			+ "AND vi.managementStartDate<=:date " 
			+ "AND vi.collectionStartDate<=:date " 
			+ "AND vi.collectionEndDate>:date"
			+ ") "
			+ "AND LOWER(camp.id) LIKE LOWER(concat('%',:text,'%')) " 
			+ "GROUP BY camp.id ")
	List<VerifyNameResponseDto> findMatchingCampaigns(@Param("text") String text, @Param("ouIds") List<String> ouIds,
			@Param("date") Long date, Pageable pageable);
	
	@Query("SELECT new fr.insee.pearljam.api.dto.message.VerifyNameResponseDto(camp.id,  'campaign', camp.label) "
			+ "FROM Campaign camp " 
			+ "INNER JOIN Visibility vi ON vi.campaign.id = camp.id "
			+ "WHERE (vi.organizationUnit.id in (:ouIds) OR 'GUEST' in (:ouIds)) "
			+ "GROUP BY camp.id ")
	List<VerifyNameResponseDto> findMatchingCampaignsByOuForAll(@Param("ouIds") List<String> ouIds);

}
