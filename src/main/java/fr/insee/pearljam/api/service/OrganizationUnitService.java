package fr.insee.pearljam.api.service;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitTreeDto;
import fr.insee.pearljam.api.exception.OrganisationUnitAlreadyExistsException;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;
import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitContextDto;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
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
