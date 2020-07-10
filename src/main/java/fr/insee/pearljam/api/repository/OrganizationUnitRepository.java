package fr.insee.pearljam.api.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.OrganizationUnit;

public interface OrganizationUnitRepository extends JpaRepository<OrganizationUnit, String> {

	Optional<OrganizationUnit> findByIdIgnoreCase(String Id);

  /**
	* This method retrieves the ids of children organizationUnit
	* 
	* @return List of all {@link String}
	*/
	@Query(value="SELECT id FROM organization_unit WHERE organization_unit_parent_id =?1", nativeQuery=true)
	List<String> findChildren(String OrgUnitId);
}
