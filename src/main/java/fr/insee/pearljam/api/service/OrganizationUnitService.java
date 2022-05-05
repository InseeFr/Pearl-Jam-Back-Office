package fr.insee.pearljam.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.domain.Response;
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

	Response createOrganizationUnits(List<OrganizationUnitContextDto> organizationUnits) throws NoOrganizationUnitException, UserAlreadyExistsException;

	List<OrganizationUnitContextDto> findAllOrganizationUnits();

	Optional<OrganizationUnitDto>  findById(String ouId);

	boolean isPresent(String ouId);

	HttpStatus delete(String id);
}
