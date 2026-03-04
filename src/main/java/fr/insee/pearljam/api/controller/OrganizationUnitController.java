package fr.insee.pearljam.api.controller;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitContextDto;
import fr.insee.pearljam.api.dto.user.UserContextDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.OrganisationUnitAlreadyExistsException;
import fr.insee.pearljam.api.exception.UserAlreadyExistsException;
import fr.insee.pearljam.api.service.OrganizationUnitService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.domain.exception.OrganizationalUnitNotFoundException;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@Tag(name = "05. Organization-units", description = "Endpoints for organization-units")
@RequiredArgsConstructor
@Slf4j
@Validated
public class OrganizationUnitController {

	private final OrganizationUnitService organizationUnitService;
	private final UserService userService;
	private final AuthenticatedUserService authenticatedUserService;

	/**
	 * This method is used to post the list of Organizational Units defined in
	 * request body
	 *
	 * @param organizationUnits organisation units to be created
	 */
	@Operation(summary = "Create Context with Organizational Unit and users associated")
	@PostMapping(Constants.API_ORGANIZATIONUNITS)
	public void postContext(@RequestBody List<OrganizationUnitContextDto> organizationUnits)
			throws NoOrganizationUnitException, OrganizationalUnitNotFoundException, UserAlreadyExistsException, OrganisationUnitAlreadyExistsException {
		organizationUnitService.createOrganizationUnits(organizationUnits);
	}

	/**
	 * This method is used to post the Organization Unit defined in request body
	 *
	 */
	@Operation(summary = "Create Organizational Unit and users associated")
	@PostMapping(Constants.API_ORGANIZATIONUNIT)
	public void postOrganizationUnit(@RequestBody OrganizationUnitContextDto organizationUnit)
			throws OrganisationUnitAlreadyExistsException, OrganizationalUnitNotFoundException, NoOrganizationUnitException, UserAlreadyExistsException {
		organizationUnitService.createOrganizationUnits(Collections.singletonList(organizationUnit));
	}

	/**
	 * This method add Users to target Organization Unit
	 *
	 * @param id campaign id
	 * @param users users to add to the target OU
	 */
	@Operation(summary = "Create users by organization-unit")
	@PostMapping(Constants.API_ORGANIZATIONUNIT_ID_USERS)
	public ResponseEntity<Object> postUsersByOrganizationUnit(@PathVariable(value = "id") String id, 
															  @RequestBody List<UserContextDto> users) {
		Response response;
		try {
			response = userService.createUsersByOrganizationUnit(users, id);
		} catch (UserAlreadyExistsException | NoOrganizationUnitException e) {
			response = new Response(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		log.info("POST /organization-unit/{}/users resulting in {} with response [{}]", id, response.getHttpStatus(),
				response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}

	/**
	 * This method return a list of all Organization Units
	 * 
	 */
	@Operation(summary = "Get all organization-units")
	@GetMapping(Constants.API_ORGANIZATIONUNITS)
	public ResponseEntity<List<OrganizationUnitContextDto>> getOrganizationUnits() {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to get all OUs", userId);
		return new ResponseEntity<>(organizationUnitService.findAllOrganizationUnits(), HttpStatus.OK);
	}

	/**
	 * This method try to delete target Organization Unit
	 *
	 * @param id id of the OU to delete
	 */
	@Operation(summary = "Delete an organization-unit")
	@DeleteMapping(Constants.API_ORGANIZATIONUNIT_ID)
	public ResponseEntity<Object> deleteOrganizationUnit(@PathVariable(value = "id") String id) {
		String userId = authenticatedUserService.getCurrentUserId();
		HttpStatus response = organizationUnitService.delete(id);
		log.info("{} : DELETE User resulting in {}", userId, response);
		return new ResponseEntity<>(response);
	}
}
