package fr.insee.pearljam.infrastructure.persistence.campaign.jpa;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.domain.reporting.query.CampaignQueryResponse;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.contracts.campaign.dto.CampaignDto;
import fr.insee.pearljam.contracts.campaign.dto.CampaignPreferenceDto;
import fr.insee.pearljam.contracts.message.dto.VerifyNameResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * CampaignRepository is the repository using to access to Campaign table in DB
 * 
 * @author scorcaud
 * 
 */
public interface CampaignJpaRepository extends JpaRepository<CampaignDB, String> {

	Optional<CampaignDB> findByIdIgnoreCase(String id);

	@Query(value = "SELECT DISTINCT(campaign_id) FROM visibility WHERE "
			+ "organization_unit_id IN (:OuIds) ", nativeQuery = true)
	List<String> findAllCampaignIdsByOuIds(@Param("OuIds") List<String> ouIds);

	@Query("SELECT DISTINCT new fr.insee.pearljam.domain.reporting.query.CampaignQueryResponse(v.visibilityId.campaignId, v.campaign.label) " +
			"FROM VisibilityDB v JOIN v.campaign " +
			"WHERE v.visibilityId.organizationUnitId IN (:ouIds) " +
			"AND v.endDate > :date")
	List<CampaignQueryResponse> findAllOpenedCampaignByOuIds(@Param("ouIds") List<String> ouIds, @Param("date") Long date);

	@Query(value = """
		SELECT DISTINCT(campaign_id) FROM visibility
		WHERE organization_unit_id IN (:ouIds)
		AND management_start_date <= :date
		AND end_date > :date
		""", nativeQuery = true)
	List<String> findAllManagedAndNotClosedCampaignIdsByOuIds(@Param("ouIds") List<String> ouIds, @Param("date") Long date);


	@Query("""
		SELECT DISTINCT new fr.insee.pearljam.contracts.campaign.dto.CampaignDto(
		    camp.id,
		    camp.label,
		    camp.email,
		    camp.identificationConfiguration,
		    camp.contactOutcomeConfiguration,
		    camp.contactAttemptConfiguration,
		    camp.collectNextContacts
		)
		FROM CampaignDB camp
		JOIN camp.visibilities vi
		JOIN vi.organizationUnit ou
		WHERE vi.managementStartDate <= :date
		AND vi.endDate > :date
		AND NOT EXISTS (
		    SELECT 1
		    FROM UserDB u
		    JOIN u.campaigns c2
		    WHERE LOWER(u.id) = LOWER(:userId)
		    AND c2 = camp
		)
		AND ou.id in (:ouIds)
		""")
	List<CampaignDto> findByUserAndManagementVisibility(
			@Param("ouIds") List<String> ouIds,
			@Param("userId") String userId,
			@Param("date") Long date
	);


	/**
	 * WARNING: negative pref !!! If pref is found it returns false (sic)
	 * @param ouIds organisational unit ids
	 * @param userId user id
	 * @param date date to check
	 * @return the campaign preferences
	 */
	@Query("""
	SELECT DISTINCT new fr.insee.pearljam.contracts.campaign.dto.CampaignPreferenceDto(
	  camp.id,
	  camp.label,
	  CASE WHEN EXISTS (
	    SELECT 1
	    FROM UserDB u2
	    JOIN u2.campaigns c2
	    WHERE LOWER(u2.id) = LOWER(:userId)
	    AND c2 = camp
	  ) THEN FALSE ELSE TRUE END
	)
	FROM CampaignDB camp
	  JOIN camp.visibilities vi
	  JOIN vi.organizationUnit ou
	WHERE ou.id in (:ouIds)
	AND vi.managementStartDate <= :date
	AND vi.endDate > :date
	""")
	List<CampaignPreferenceDto> findByOuIdWithPreference(
			@Param("ouIds") List<String> ouIds,
			@Param("userId") String userId,
			@Param("date") Long date
	);


	@Query(value = "SELECT new fr.insee.pearljam.contracts.campaign.dto.CampaignDto(camp.id, camp.label, camp.email, camp.identificationConfiguration, camp.contactOutcomeConfiguration, camp.contactAttemptConfiguration, camp.collectNextContacts) "
			+ "FROM CampaignDB camp "
			+ "WHERE camp.id=?1")
	CampaignDto findDtoById(String id);

	@Query("SELECT "
			+ "new fr.insee.pearljam.contracts.campaign.dto.CampaignDto(camp.id, camp.label, camp.email, camp.identificationConfiguration, camp.contactOutcomeConfiguration, camp.contactAttemptConfiguration, camp.collectNextContacts) "
			+ "FROM SurveyUnitDB su "
			+ "JOIN su.campaign camp "
			+ "WHERE su.id=?1")
	CampaignDto findDtoBySurveyUnitId(String id);

	@Query(value = "SELECT new fr.insee.pearljam.contracts.campaign.dto.CampaignDto(camp.id, camp.label, camp.email, camp.identificationConfiguration, camp.contactOutcomeConfiguration, camp.contactAttemptConfiguration, camp.collectNextContacts) "
			+ "FROM CampaignDB camp")
	List<CampaignDto> findAllDto();

	@Query("""
    SELECT DISTINCT new fr.insee.pearljam.contracts.campaign.dto.CampaignDto(
        camp.id, camp.label, camp.email,
        camp.identificationConfiguration,
        camp.contactOutcomeConfiguration,
        camp.contactAttemptConfiguration,
        camp.collectNextContacts
    )
    FROM CampaignDB camp
    JOIN camp.visibilities vi
    WHERE vi.organizationUnit.id IN :ouIds
    """)
	List<CampaignDto> findAllDtoByOuIds(@Param("ouIds") List<String> ouIds);

	@Query(value = "SELECT v.organization_unit_id FROM visibility v WHERE v.campaign_id=?1", nativeQuery = true)
	List<String> findAllOrganistionUnitIdByCampaignId(String campaignId);

	@Query("SELECT new fr.insee.pearljam.contracts.message.dto.VerifyNameResponseDto(camp.id,  'campaign', camp.label) "
			+ "FROM CampaignDB camp "
			+ "JOIN camp.visibilities vi "
			+ "WHERE ("
			+ "vi.organizationUnit.id in (:ouIds) "
			+ "OR 'GUEST' in (:ouIds) "
			+ "AND vi.managementStartDate<=:date "
			+ "AND vi.collectionStartDate<=:date "
			+ "AND vi.collectionEndDate>:date"
			+ ") "
			+ "AND LOWER(camp.id) LIKE LOWER(concat('%',:text,'%')) "
			+ "GROUP BY camp.id ")
	List<VerifyNameResponseDto> findMatchingCampaigns(@Param("text") String text, @Param("ouIds") List<String> ouIds,
			@Param("date") Long date, Pageable pageable);
}
