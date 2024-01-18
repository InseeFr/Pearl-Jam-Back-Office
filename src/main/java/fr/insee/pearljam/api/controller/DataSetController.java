package fr.insee.pearljam.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.service.DataSetInjectorService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "10. Data injector", description = "Endpoints for data injection/deletion")
@RequestMapping(path = "/api")
@RequiredArgsConstructor
@Slf4j
public class DataSetController {

	private final DataSetInjectorService injector;
	private final UtilsService utilsService;

	@Operation(summary = "Create dataset")
	@PostMapping(path = "/create-dataset")
	public ResponseEntity<Object> createDataSet() {
		if (!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		HttpStatus status = injector.createDataSet();
		return new ResponseEntity<>(status);
	}

	@Operation(summary = "Delete dataset")
	@DeleteMapping(path = "/delete-dataset")
	public ResponseEntity<Object> deteteDataSet() {
		if (!utilsService.isDevProfile() && !utilsService.isTestProfile()) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		HttpStatus status = injector.deleteDataSet();

		log.info("Dataset deletion end");
		return new ResponseEntity<>(status);
	}

}
