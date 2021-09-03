package fr.insee.pearljam.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.service.DataSetInjectorService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping(path = "/api")
public class DataSetController {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSetController.class);
	@Autowired
	private DataSetInjectorService injector;
	
	@Autowired
	UtilsService utilsService;
	
	@ApiOperation(value = "Create dataset")
	@PostMapping(path = "/create-dataset")
	public ResponseEntity<Object> createDataSet() {
		if(!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		HttpStatus status = injector.createDataSet();
		return new ResponseEntity<>(status);
	}
	
	@ApiOperation(value = "Delete dataset")
	@DeleteMapping(path = "/delete-dataset")
	public ResponseEntity<Object> deteteDataSet() {
		if(!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		
		HttpStatus status = injector.deleteDataSet();
		
		LOGGER.info("Dataset deletion end");	
		return new ResponseEntity<>(status);
	}

	

}
