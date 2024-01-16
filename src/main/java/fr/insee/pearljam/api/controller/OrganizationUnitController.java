package fr.insee.pearljam.api.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitContextDto;
import fr.insee.pearljam.api.dto.user.UserContextDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.UserAlreadyExistsException;
import fr.insee.pearljam.api.service.OrganizationUnitService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@Slf4j
public class OrganizationUnitController {

	private final OrganizationUnitService organizationUnitService;

	private final UserService userService;

	private final UtilsService utilsService;

	/**
	 * This method is used to post the list of Organizational Units defined in
	 * request body
	 * 
	 * @param request
	 * @param idCampaign
	 * @param idOu
	 */
	@ApiOperation(value = "Create Context with Organizational Unit and users associated")
	@PostMapping(path = "/organization-units")
	public ResponseEntity<Object> postContext(HttpServletRequest request,
			@RequestBody List<OrganizationUnitContextDto> organizationUnits) {

		Response response;
		try {
			response = organizationUnitService.createOrganizationUnits(organizationUnits);
		} catch (NoOrganizationUnitException | UserAlreadyExistsException e) {
			response = new Response(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		log.info("POST /organization-units resulting in {} with response [{}]", response.getHttpStatus(),
				response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}

	/**
	 * This method is used to post the Organization Unit defined in request body
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Create Organizational Unit and users associated")
	@PostMapping(path = "/organization-unit")
	public ResponseEntity<Object> postOrganizationUnit(HttpServletRequest request,
			@RequestBody OrganizationUnitContextDto organizationUnit) {
		String callerId = utilsService.getUserId(request);
		log.info("{} try to create a new OU", callerId);
		Response response;
		try {
			response = organizationUnitService.createOrganizationUnits(Collections.singletonList(organizationUnit));
		} catch (NoOrganizationUnitException | UserAlreadyExistsException e) {
			log.error(e.getMessage());
			response = new Response(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		log.info("{} POST /organization-unit resulting in {} with response [{}]", callerId, response.getHttpStatus(),
				response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}

	/**
	 * This method add Users to target Organization Unit
	 * 
	 * @param request
	 * @param idCampaign
	 * @param idOu
	 */
	@ApiOperation(value = "Create users by organization-unit")
	@PostMapping(path = "/organization-unit/{id}/users")
	public ResponseEntity<Object> postUsersByOrganizationUnit(HttpServletRequest request,
			@PathVariable(value = "id") String id, @RequestBody List<UserContextDto> users) {

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
	 * @param request
	 * @param idCampaign
	 * @param idOu
	 */
	@ApiOperation(value = "Get all organization-units")
	@GetMapping(path = "/organization-units")
	public ResponseEntity<List<OrganizationUnitContextDto>> getOrganizationUnits(HttpServletRequest request) {
		String callerId = utilsService.getUserId(request);
		log.info("{} try to get all OUs", callerId);
		return new ResponseEntity<>(organizationUnitService.findAllOrganizationUnits(), HttpStatus.OK);
	}

	/**
	 * This method try to delete target Organization Unit
	 * 
	 * @param request
	 * @param idCampaign
	 * @param idOu
	 */
	@ApiOperation(value = "Delete an organization-unit")
	@DeleteMapping(path = "/organization-unit/{id}")
	public ResponseEntity<Object> deleteOrganizationUnit(HttpServletRequest request,
			@PathVariable(value = "id") String id) {
		HttpStatus response = organizationUnitService.delete(id);
		log.info("DELETE User resulting in {}", response);
		return new ResponseEntity<>(response);
	}

}
