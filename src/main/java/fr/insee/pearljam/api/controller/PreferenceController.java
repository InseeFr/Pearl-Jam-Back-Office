package fr.insee.pearljam.api.controller;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.service.PreferenceService;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
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
	@PutMapping(Constants.API_PREFERENCES)
	public ResponseEntity<Object> updateSurveyUnit(@RequestBody List<String> listPreference) {
		String userId = authenticatedUserService.getCurrentUserId();
		HttpStatus returnCode = preferenceService.setPreferences(listPreference, userId);
		log.info("PUT preferences '{}' for user {} resulting in {}", String.join(", ", listPreference), userId,
				returnCode.value());
		return new ResponseEntity<>(returnCode);
	}
}
