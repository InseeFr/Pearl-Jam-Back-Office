package fr.insee.pearljam.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.insee.pearljam.api.domain.OrganizationUnit;

/**
* OrganizationUnitRepository is the repository using to access to Organisation unit table in DB
* 
* @author Claudel Benjamin
* 
*/
public interface OrganizationUnitRepository extends JpaRepository<OrganizationUnit, String> {

	@Query(value="SELECT id FROM organization_unit WHERE organization_unit_parent_id =?1", nativeQuery=true)
	List<String> findChildrenId(String orgUnitId);
	
	@Query("SELECT ou FROM OrganizationUnit ou WHERE ou.organizationUnitParent.id =?1")
	List<OrganizationUnit> findChildren(String orgUnitId);
	
	@Query("SELECT id FROM OrganizationUnit")
	List<String> findAllId();
}
