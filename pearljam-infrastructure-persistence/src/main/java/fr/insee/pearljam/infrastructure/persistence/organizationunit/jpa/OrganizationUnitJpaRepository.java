package fr.insee.pearljam.infrastructure.persistence.organizationunit.jpa;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.domain.surveyunit.model.count.OrganizationUnitLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto;

/**
 * OrganizationUnitRepository is the repository using to access to Organisation
 * unit table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
public interface OrganizationUnitJpaRepository extends JpaRepository<OrganizationUnitDB, String> {

	Optional<OrganizationUnitDB> findByIdIgnoreCase(String ouId);

	@Query("""
			SELECT new fr.insee.pearljam.contracts.organizationunit.dto.OrganizationUnitDto(ou.id, ou.label)
			FROM OrganizationUnitDB ou
			WHERE lower(ou.id) = lower(:ouId)
			""")
	Optional<OrganizationUnitDto> findDtoByIdIgnoreCase(@Param("ouId") String ouId);

	@Query(value = "SELECT id FROM organization_unit WHERE organization_unit_parent_id =?1", nativeQuery = true)
	List<String> findChildrenId(String orgUnitId);

	@Query("SELECT ou FROM OrganizationUnitDB ou WHERE ou.organizationUnitParent.id =?1")
	List<OrganizationUnitDB> findChildren(String orgUnitId);

	@Query("SELECT id FROM OrganizationUnitDB")
	List<String> findAllId();

	@Query("SELECT label FROM OrganizationUnitDB ou WHERE ou.id =?1")
	String findLabel(String orgUnitId);

	@Query(value = """
			SELECT id, label, type, organization_unit_parent_id
			FROM organization_unit
			WHERE id = :rootId
			   OR organization_unit_parent_id = :rootId;
			"""
			, nativeQuery = true)
	List<OrganizationUnitDB> findSubtree(@Param("rootId") String rootId);


	@Query("""
        select ou.id as id, ou.label as label
        from OrganizationUnitDB ou
        where ou.id in :ids
    """)
	List<OrganizationUnitLabel> findLabelsByIds(@Param("ids") List<String> ids);
}
