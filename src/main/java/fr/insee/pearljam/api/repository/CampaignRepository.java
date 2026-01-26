package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.campaign.CampaignPreferenceDto;
import fr.insee.pearljam.api.dto.message.VerifyNameResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;

/**
 * CampaignRepository is the repository using to access to Campaign table in DB
 * 
 * @author scorcaud
 * 
 */
public interface CampaignRepository extends JpaRepository<Campaign, String> {

	Optional<Campaign> findByIdIgnoreCase(String id);

	@Query(value = "SELECT DISTINCT(campaign_id) FROM visibility WHERE "
			+ "organization_unit_id IN (:OuIds) ", nativeQuery = true)
	List<String> findAllCampaignIdsByOuIds(@Param("OuIds") List<String> ouIds);

	@Query("""
	SELECT DISTINCT new fr.insee.pearljam.api.dto.campaign.CampaignDto(
	  camp.id,
	  camp.label,
	  camp.email,
	  camp.identificationConfiguration,
	  camp.contactOutcomeConfiguration,
	  camp.contactAttemptConfiguration
	)
	FROM User u
	  JOIN u.campaigns camp
	  JOIN camp.visibilities vi
	WHERE LOWER(u.id) = LOWER(:userId)
	AND vi.managementStartDate <= :date
	AND vi.collectionEndDate > :date
	""")
	List<CampaignDto> findByUserAndManagementVisibility(
			@Param("userId") String userId,
			@Param("date") Long date
	);


	@Query("""
	SELECT DISTINCT new fr.insee.pearljam.api.dto.campaign.CampaignPreferenceDto(
	  camp.id,
	  camp.label,
	  CASE WHEN EXISTS (
	    SELECT 1
	    FROM User u2
	    JOIN u2.campaigns c2
	    WHERE LOWER(u2.id) = LOWER(:userId)
	    AND c2 = camp
	  ) THEN TRUE ELSE FALSE END
	)
	FROM Campaign camp
	  JOIN camp.visibilities vi
	  JOIN vi.organizationUnit ou
	WHERE ou.id in (:ouIds)
	AND vi.managementStartDate <= :date
	AND vi.collectionEndDate > :date
	""")
	List<CampaignPreferenceDto> findByOuIdWithPreference(
			@Param("ouIds") List<String> ouIds,
			@Param("userId") String userId,
			@Param("date") Long date
	);



	@Query(value = "SELECT new fr.insee.pearljam.api.dto.campaign.CampaignDto(camp.id, camp.label, camp.email, camp.identificationConfiguration, camp.contactOutcomeConfiguration, camp.contactAttemptConfiguration) "
			+ "FROM Campaign camp "
			+ "WHERE camp.id=?1")
	CampaignDto findDtoById(String id);

	@Query("SELECT "
			+ "new fr.insee.pearljam.api.dto.campaign.CampaignDto(camp.id, camp.label, camp.email, camp.identificationConfiguration, camp.contactOutcomeConfiguration, camp.contactAttemptConfiguration) "
			+ "FROM SurveyUnit su "
			+ "JOIN su.campaign camp "
			+ "WHERE su.id=?1")
	CampaignDto findDtoBySurveyUnitId(String id);

	@Query(value = "SELECT new fr.insee.pearljam.api.dto.campaign.CampaignDto(camp.id, camp.label, camp.email, camp.identificationConfiguration, camp.contactOutcomeConfiguration, camp.contactAttemptConfiguration) "
			+ "FROM Campaign camp")
	List<CampaignDto> findAllDto();

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
			+ "JOIN camp.visibilities vi "
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
}
