package fr.insee.pearljam.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.domain.Response;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitContextDto;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
import fr.insee.pearljam.api.exception.UserAlreadyExistsException;
import fr.insee.pearljam.api.service.OrganizationUnitService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/api")
public class OrganizationUnitController {

	private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationUnitController.class);
	@Autowired
	OrganizationUnitService organizationUnitService;
	
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
}
