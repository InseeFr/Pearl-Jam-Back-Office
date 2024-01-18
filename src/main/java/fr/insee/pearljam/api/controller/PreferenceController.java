package fr.insee.pearljam.api.controller;

import java.util.List;

import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.service.PreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api")
public class PreferenceController {

	private final PreferenceService preferenceService;
	private final AuthenticatedUserService authenticatedUserService;

	/**
	 * This method is using to update the state of Survey Units listed in request
	 * body
	 * 
	 * @param request
	 * @param listPreference
	 * @return
	 */
	@Operation(summary = "Update preferences with campaigns listed in request body")
	@PutMapping(path = "/preferences")
	public ResponseEntity<Object> updateSurveyUnit(@RequestBody List<String> listPreference) {
		String userId = authenticatedUserService.getCurrentUserId();
		HttpStatus returnCode = preferenceService.setPreferences(listPreference, userId);
		log.info("PUT preferences '{}' for user {} resulting in {}", String.join(", ", listPreference), userId,
				returnCode.value());
		return new ResponseEntity<>(returnCode);
	}
}
