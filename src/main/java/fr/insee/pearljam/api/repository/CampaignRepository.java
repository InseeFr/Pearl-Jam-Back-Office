package fr.insee.pearljam.api.repository;

import java.util.List;
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

	@Query(value = "SELECT DISTINCT(campaign_id) FROM visibility WHERE "
			+ "organization_unit_id IN (:OuIds) ", nativeQuery = true)
	List<String> findAllCampaignIdsByOuIds(@Param("OuIds") List<String> ouIds);

	@Query(value = "SELECT camp.id "
			+ "FROM campaign camp "
			+ "INNER JOIN visibility vi ON vi.campaign_id = camp.id "
			+ "INNER JOIN organization_unit ou ON ou.id = vi.organization_unit_id "
			+ "WHERE ou.id ILIKE ?1", nativeQuery = true)
	List<String> findIdsByOuId(String ouId);

	@Query(value = "SELECT new fr.insee.pearljam.api.dto.campaign.CampaignDto(camp.id, camp.label, camp.email, camp.identificationConfiguration, camp.contactOutcomeConfiguration, camp.contactAttemptConfiguration, camp.communicationConfiguration) "
			+ "FROM Campaign camp "
			+ "WHERE camp.id=?1")
	CampaignDto findDtoById(String id);

	@Query("SELECT "
			+ "new fr.insee.pearljam.api.dto.campaign.CampaignDto(camp.id, camp.label, camp.email, camp.identificationConfiguration, camp.contactOutcomeConfiguration, camp.contactAttemptConfiguration, camp.communicationConfiguration) "
			+ "FROM SurveyUnit su "
			+ "JOIN su.campaign camp "
			+ "WHERE su.id=?1")
	CampaignDto findDtoBySurveyUnitId(String id);

	@Query(value = "SELECT new fr.insee.pearljam.api.dto.campaign.CampaignDto(camp.id, camp.label, camp.email, camp.identificationConfiguration, camp.contactOutcomeConfiguration, camp.contactAttemptConfiguration, camp.communicationConfiguration) "
			+ "FROM Campaign camp")
	List<CampaignDto> findAllDto();

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

	@Query(value = "SELECT v.organization_unit_id FROM visibility v WHERE v.campaign_id=?1", nativeQuery = true)
	List<String> findAllOrganistionUnitIdByCampaignId(String campaignId);

	@Query("SELECT new fr.insee.pearljam.api.dto.message.VerifyNameResponseDto(camp.id,  'campaign', camp.label) "
			+ "FROM Campaign camp "
			+ "INNER JOIN Visibility vi ON vi.campaign.id = camp.id "
			+ "WHERE ("
			+ "vi.organizationUnit.id in (:ouIds) "
			+ "OR 'GUEST' in (:ouIds) "
			+ "AND vi.managementStartDate<=:date "
			+ "AND vi.managementStartDate<=:date "
			+ "AND vi.managementStartDate<=:date "
			+ "AND vi.collectionStartDate<=:date "
			+ "AND vi.collectionStartDate<=:date "
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
			+ "WHERE vi.organizationUnit.id in (:ouIds) OR 'GUEST' in (:ouIds) "
			+ "GROUP BY camp.id ")
	List<VerifyNameResponseDto> findMatchingCampaignsByOuForAll(@Param("ouIds") List<String> ouIds);

}
