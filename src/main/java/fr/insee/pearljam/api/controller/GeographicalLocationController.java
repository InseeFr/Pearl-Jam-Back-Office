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
import fr.insee.pearljam.api.dto.geographicallocation.GeographicalLocationDto;
import fr.insee.pearljam.api.service.GeographicalLocationService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/api")
public class GeographicalLocationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeographicalLocationController.class);
	@Autowired
	GeographicalLocationService geographicalLocationService;
	
	@Autowired
	UtilsService utilsService;
	
	/**
	 * This method is using to post the list of geographical-locations defined in request body
	 * 
	 * @param request
	 * @param geographicalLocations
	 */
	 @ApiOperation(value = "Create Context with Organizational Unit and users associated")
	 @PostMapping(path = "/geographical-locations")
	 public ResponseEntity<Object> postContext(HttpServletRequest request, @RequestBody List<GeographicalLocationDto> geographicalLocations){
		if(!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		Response response = geographicalLocationService.createGeographicalLocations(geographicalLocations);
		LOGGER.info("POST /geographical-locations resulting in {} with response [{}]", response.getHttpStatus(), response.getMessage());
		return new ResponseEntity<>(response.getMessage(), response.getHttpStatus());
	 }
}
