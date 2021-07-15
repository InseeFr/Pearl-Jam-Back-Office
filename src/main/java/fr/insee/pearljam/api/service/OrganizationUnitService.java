package fr.insee.pearljam.api.service;

import java.util.List;

import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitContextDto;
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

	HttpStatus delete(String id);
}
