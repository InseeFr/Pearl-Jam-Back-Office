package fr.insee.pearljam.domain.organizationunit.port.userside;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitTreeDto;
import fr.insee.pearljam.api.exception.OrganisationUnitAlreadyExistsException;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;
import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitContextDto;
import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.UserAlreadyExistsException;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface OrganizationUnitService {

	void createOrganizationUnits(List<OrganizationUnitContextDto> organizationUnits) throws NoOrganizationUnitException, UserAlreadyExistsException, OrganizationalUnitNotFoundException, OrganisationUnitAlreadyExistsException;

	List<OrganizationUnitContextDto> findAllOrganizationUnits();

	Optional<OrganizationUnitDto>  findById(String ouId);

	boolean isPresent(String ouId);

	HttpStatus delete(String id);

	OrganizationUnitTreeDto getOrganizationUnitTree(String rootId, boolean saveAllLevels);
}
