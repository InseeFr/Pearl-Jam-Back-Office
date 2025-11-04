package fr.insee.pearljam.organization.domain.port.userside;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import fr.insee.pearljam.shared.Response;
import fr.insee.pearljam.organization.infrastructure.rest.dto.OrganizationUnitContextDto;
import fr.insee.pearljam.organization.infrastructure.rest.dto.OrganizationUnitDto;
import fr.insee.pearljam.organization.domain.service.exception.NoOrganizationUnitException;
import fr.insee.pearljam.organization.domain.service.exception.UserAlreadyExistsException;

/**
 * Service for the Campaign entity
 * 
 * @author scorcaud
 *
 */
public interface OrganizationUnitService {

	Response createOrganizationUnits(List<OrganizationUnitContextDto> organizationUnits) throws NoOrganizationUnitException, UserAlreadyExistsException;

	List<OrganizationUnitContextDto> findAllOrganizationUnits();

	Optional<OrganizationUnitDto>  findById(String ouId);

	boolean isPresent(String ouId);

	HttpStatus delete(String id);
}
