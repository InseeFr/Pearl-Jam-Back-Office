package fr.insee.pearljam.domain.organizationunit.port.in;

import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitTreeDto;
import fr.insee.pearljam.domain.organizationunit.service.exception.OrganisationUnitAlreadyExistsException;
import fr.insee.pearljam.domain.campaign.service.exception.OrganizationalUnitNotFoundException;
import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitContextDto;
import fr.insee.pearljam.api.organizationunit.dto.OrganizationUnitDto;
import fr.insee.pearljam.domain.organizationunit.service.exception.NoOrganizationUnitException;
import fr.insee.pearljam.domain.organizationunit.service.exception.UserAlreadyExistsException;

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
