package fr.insee.pearljam.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping(path = "/api")
public class OrganizationUnitController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationUnitController.class);
	@Autowired
	OrganizationUnitService organizationUnitService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UtilsService utilsService;
	
	/**
	 * This method is using to post the list of SurveyUnit defined in request body
	 * 
	 * @param request
	 * @param idCampaign
	 * @param idOu
	 */
	@ApiOperation(value = "Create Context with Organizational Unit and users associated")
	@PostMapping(path = "/organization-units")
	public ResponseEntity<Object> postContext(HttpServletRequest request, @RequestBody List<OrganizationUnitContextDto> organizationUnits){
		if(!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Response response;
		try {
			response = organizationUnitService.createOrganizationUnits(organizationUnits);
		} catch (NoOrganizationUnitException | UserAlreadyExistsException e) {
			response = new Response(e.getMessage(), HttpStatus.BAD_REQUEST);
		} 
		LOGGER.info("POST /organization-units resulting in {} with response [{}]", response.getHttpStatus(), response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}
	
	/**
	 * This method is using to post the list of SurveyUnit defined in request body
	 * 
	 * @param request
	 * @param idCampaign
	 * @param idOu
	 */
	@ApiOperation(value = "Create users by organization-unit")
	@PostMapping(path = "/organization-unit/{id}/users")
	public ResponseEntity<Object> postUsersByOrganizationUnit(HttpServletRequest request, @PathVariable(value = "id") String id, @RequestBody List<UserContextDto> users){
		if(!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Response response;
		try {
			response = userService.createUsersByOrganizationUnit(users, id);
		} catch (UserAlreadyExistsException | NoOrganizationUnitException e) {
			response = new Response(e.getMessage(), HttpStatus.BAD_REQUEST);
		} 
		LOGGER.info("POST /organization-unit/{id}/users resulting in {} with response [{}]", response.getHttpStatus(), response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	}
	
	/**
	 * This method is using to post the list of SurveyUnit defined in request body
	 * 
	 * @param request
	 * @param idCampaign
	 * @param idOu
	 */
	@ApiOperation(value = "Get all organization-units")
	@GetMapping(path = "/organization-units")
	public ResponseEntity<List<OrganizationUnitContextDto>> getOrganizationUnits(HttpServletRequest request){
		return new ResponseEntity<>(organizationUnitService.findAllOrganizationUnits(), HttpStatus.OK);
	}
	
	/**
	 * This method is using to post the list of SurveyUnit defined in request body
	 * 
	 * @param request
	 * @param idCampaign
	 * @param idOu
	 */
	@ApiOperation(value = "Delete an organization-unit")
	@DeleteMapping(path = "/organization-unit/{id}")
	public ResponseEntity<Object> deleteOrganizationUnit(HttpServletRequest request, @PathVariable(value = "id") String id){
		HttpStatus response = organizationUnitService.delete(id);
		LOGGER.info("DELETE User resulting in {}", response);
		return new ResponseEntity<>(response);
	}
	
}
