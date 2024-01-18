package fr.insee.pearljam.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;

/**
 * OrganizationUnitRepository is the repository using to access to Organisation
 * unit table in DB
 * 
 * @author Claudel Benjamin
 * 
 */
public interface OrganizationUnitRepository extends JpaRepository<OrganizationUnit, String> {

	Optional<OrganizationUnit> findByIdIgnoreCase(String ouId);

	@Query("""
			SELECT new fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto(ou.id, ou.label)
			FROM OrganizationUnit ou
			WHERE lower(ou.id) = lower(:ouId)
			""")
	Optional<OrganizationUnitDto> findDtoByIdIgnoreCase(@Param("ouId") String ouId);

	@Query(value = "SELECT id FROM organization_unit WHERE organization_unit_parent_id =?1", nativeQuery = true)
	List<String> findChildrenId(String orgUnitId);

	@Query(value = "SELECT id FROM organization_unit WHERE type = 'NATIONAL'", nativeQuery = true)
	List<String> findNationalOUs();

	@Query("SELECT ou FROM OrganizationUnit ou WHERE ou.organizationUnitParent.id =?1")
	List<OrganizationUnit> findChildren(String orgUnitId);

	@Query("SELECT id FROM OrganizationUnit")
	List<String> findAllId();

	@Query("SELECT label FROM OrganizationUnit ou WHERE ou.id =?1")
	String findLabel(String orgUnitId);

}
